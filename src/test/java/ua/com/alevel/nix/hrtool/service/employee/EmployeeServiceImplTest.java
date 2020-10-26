package ua.com.alevel.nix.hrtool.service.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.employee.EmployeeName;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveEmployeeRequest;
import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;
import ua.com.alevel.nix.hrtool.model.position.Position;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;
import ua.com.alevel.nix.hrtool.repository.EmployeeRepository;
import ua.com.alevel.nix.hrtool.repository.PositionRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {
    private EmployeeRepository employeeRepository;

    private PositionRepository positionRepository;

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        positionRepository = mock(PositionRepository.class);
        employeeService = new EmployeeServiceImpl(employeeRepository, positionRepository);
    }

    @Test
    void findAll() {
        Employee emp1 = new Employee(1L, "employee@mail.com", new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        Employee emp2 = new Employee(2L, "employee2@mail.com", new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        List<Position> positions = List.of(new Position(1L, "position"), new Position(2L, "position2"));
        emp1.setPositions(new HashSet<>(positions));

        Page<Employee> pageableEmployees = new PageImpl<>(List.of(emp1, emp2));
        Pageable pageable = PageRequest.of(0, 20);

        when(employeeRepository.findAll(pageable)).thenReturn(pageableEmployees);

        Page<EmployeeResponse> response = employeeService.findAll(pageable);

        assertEmployeeToResponse(response.getContent().get(0), emp1);
        assertTrue(
                response.getContent().get(0).getPositions().containsAll(
                        positions.stream()
                                .map(PositionResponse::fromPositionWithBasicAttributes)
                                .collect(Collectors.toList())
                )
        );
        assertEmployeeToResponse(response.getContent().get(1), emp2);
        assertNull(response.getContent().get(1).getPositions());
        verify(employeeRepository).findAll(pageable);

        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void create() {
        testUniqueEmailOnCreate();
        testInvalidPositionsOnCreate();
        testValidDataOnCreate();
        verifyNoMoreInteractions(employeeRepository, positionRepository);
    }

    private void testUniqueEmailOnCreate() {
        SaveEmployeeRequest request = new SaveEmployeeRequest("duplicate@email.com", null, null, null, null);

        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> employeeService.create(request))
                .satisfies(e -> assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()));
        verify(employeeRepository).existsByEmail(request.getEmail());
    }

    private void testInvalidPositionsOnCreate() {
        SaveEmployeeRequest request = new SaveEmployeeRequest("invalidPositions@email.com", null, null, null, null);
        List<Long> positionIds = List.of(100L, 1000L);
        request.setPositionIds(positionIds);

        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(positionRepository.findAllById(request.getPositionIds())).thenReturn(List.of(new Position(100L, "position")));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> employeeService.create(request))
                .satisfies(e -> assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()));
        verify(employeeRepository).existsByEmail(request.getEmail());
        verify(positionRepository).findAllById(positionIds);
    }

    private void testValidDataOnCreate() {
        long employeeId = 1;
        SaveEmployeeRequest request = new SaveEmployeeRequest(
                "valid@email.com", "Name", "LastName", LocalDate.now(), LocalDate.now());
        List<Position> positions = List.of(new Position(1L, "position"), new Position(2L, "position2"));
        request.setPositionIds(positions.stream().map(Position::getId).collect(Collectors.toList()));

        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(positionRepository.findAllById(request.getPositionIds())).thenReturn(positions);
        when(employeeRepository.save(notNull())).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            assertNull(employee.getId());
            assertEquals(request.getEmail(), employee.getEmail());
            assertEquals(request.getFirstName(), employee.getEmployeeName().getFirstName());
            assertEquals(request.getLastName(), employee.getEmployeeName().getLastName());
            assertEquals(request.getBirthDate(), employee.getBirthDate());
            assertEquals(request.getHiringDate(), employee.getHiringDate());
            assertTrue(positions.containsAll(employee.getPositions()));
            employee.setId(employeeId);
            return employee;
        });

        EmployeeResponse response = employeeService.create(request);
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getFirstName(), response.getFirstName());
        assertEquals(request.getLastName(), response.getLastName());
        assertEquals(request.getBirthDate(), response.getBirthDate());
        assertEquals(request.getHiringDate(), response.getHiringDate());
        assertTrue(
                positions.stream()
                        .map(PositionResponse::fromPositionWithBasicAttributes)
                        .collect(Collectors.toList())
                        .containsAll(response.getPositions())
        );
        verify(employeeRepository).existsByEmail(request.getEmail());
        verify(positionRepository).findAllById(request.getPositionIds());
        verify(employeeRepository).save(notNull());
    }

    @Test
    void getById() {
        long absentId = 10;
        long presentId = 1;
        Employee employee = new Employee(
                presentId, "employee@mail.com", new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        List<Position> positions = List.of(new Position(1L, "position"), new Position(2L, "position2"));
        employee.setPositions(new HashSet<>(positions));
        when(employeeRepository.findById(absentId)).thenReturn(Optional.empty());
        when(employeeRepository.findById(presentId)).thenReturn(Optional.of(employee));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> employeeService.getById(absentId))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(employeeRepository).findById(absentId);

        EmployeeResponse response = employeeService.getById(presentId);
        assertEmployeeToResponse(response, employee);
        assertTrue(
                response.getPositions().containsAll(
                        positions.stream()
                                .map(PositionResponse::fromPositionWithBasicAttributes)
                                .collect(Collectors.toList())
                )
        );

        verify(employeeRepository).findById(presentId);

        verifyNoMoreInteractions(employeeRepository);
    }

    private void assertEmployeeToResponse(EmployeeResponse response, Employee employee) {
        assertEquals(employee.getId(), response.getId());
        assertEquals(employee.getEmail(), response.getEmail());
        assertEquals(employee.getEmployeeName().getFirstName(), response.getFirstName());
        assertEquals(employee.getEmployeeName().getLastName(), response.getLastName());
        assertEquals(employee.getBirthDate(), response.getBirthDate());
        assertEquals(employee.getHiringDate(), response.getHiringDate());
    }

    @Test
    void update() {
        testUnExistingEmployeeOnUpdate();
        testUniqueEmailOnUpdate();
        testInvalidPositionsOnUpdate();
        testValidDataOnUpdate();

        verifyNoMoreInteractions(employeeRepository, positionRepository);
    }

    private void testUnExistingEmployeeOnUpdate() {
        long absentId = 10;
        SaveEmployeeRequest request = new SaveEmployeeRequest("id@invalid.com", null, null, null, null);

        when(employeeRepository.findById(absentId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> employeeService.update(absentId, request))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(employeeRepository).findById(absentId);
    }

    private void testUniqueEmailOnUpdate() {
        long presentId = 9;
        Employee employee = new Employee(presentId, null, null, null, null);
        SaveEmployeeRequest request = new SaveEmployeeRequest("duplicate@email.com", null, null, null, null);

        when(employeeRepository.findById(presentId)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> employeeService.update(presentId, request))
                .satisfies(e -> assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()));
        verify(employeeRepository).findById(presentId);
        verify(employeeRepository).existsByEmail(request.getEmail());
    }

    private void testInvalidPositionsOnUpdate() {
        long presentId = 8;
        Employee employee = new Employee(presentId, null, null, null, null);
        SaveEmployeeRequest request = new SaveEmployeeRequest("invalidPositions@email.com", null, null, null, null);
        List<Long> positionIds = List.of(100L, 1000L);
        request.setPositionIds(positionIds);

        when(employeeRepository.findById(presentId)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(positionRepository.findAllById(request.getPositionIds())).thenReturn(List.of(new Position(100L, "position")));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> employeeService.update(presentId, request))
                .satisfies(e -> assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()));
        verify(employeeRepository).findById(presentId);
        verify(employeeRepository).existsByEmail(request.getEmail());
        verify(positionRepository).findAllById(positionIds);
    }

    private void testValidDataOnUpdate() {
        long presentId = 1;
        Employee employee = new Employee(presentId, "old@mail.com", new EmployeeName("first", "last"), LocalDate.now(), LocalDate.now());
        SaveEmployeeRequest request = new SaveEmployeeRequest(
                "valid@email.com", "FIRST", "LAST", LocalDate.now(), LocalDate.now());
        List<Long> positionIds = List.of(100L);
        request.setPositionIds(positionIds);

        when(employeeRepository.findById(presentId)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(positionRepository.findAllById(request.getPositionIds())).thenReturn(List.of(new Position(100L, "position")));

        employeeService.update(presentId, request);
        assertEquals(request.getEmail(), employee.getEmail());
        assertEquals(request.getFirstName(), employee.getEmployeeName().getFirstName());
        assertEquals(request.getLastName(), employee.getEmployeeName().getLastName());
        assertEquals(request.getBirthDate(), employee.getBirthDate());
        assertEquals(request.getHiringDate(), employee.getHiringDate());
        assertTrue(
                request.getPositionIds().containsAll(
                        employee.getPositions().stream().map(Position::getId).collect(Collectors.toList()))
        );
        verify(employeeRepository).findById(presentId);
        verify(employeeRepository).existsByEmail(request.getEmail());
        verify(positionRepository).findAllById(positionIds);
        verify(employeeRepository).save(employee);
    }

    @Test
    void deleteById() {
        long absentId = 10;
        long presentId = 1;
        Employee employee = new Employee();

        when(employeeRepository.findById(absentId)).thenReturn(Optional.empty());
        when(employeeRepository.findById(presentId)).thenReturn(Optional.of(employee));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> employeeService.deleteById(absentId))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(employeeRepository).findById(absentId);

        employeeService.deleteById(presentId);
        verify(employeeRepository).findById(presentId);
        verify(employeeRepository).deleteById(presentId);

        verifyNoMoreInteractions(employeeRepository);
    }
}