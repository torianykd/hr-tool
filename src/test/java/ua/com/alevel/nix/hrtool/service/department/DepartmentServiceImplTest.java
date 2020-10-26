package ua.com.alevel.nix.hrtool.service.department;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ua.com.alevel.nix.hrtool.model.department.Department;
import ua.com.alevel.nix.hrtool.model.department.request.SaveDepartmentRequest;
import ua.com.alevel.nix.hrtool.model.department.response.DepartmentResponse;
import ua.com.alevel.nix.hrtool.model.position.Position;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;
import ua.com.alevel.nix.hrtool.repository.DepartmentRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class DepartmentServiceImplTest {

    private DepartmentServiceImpl departmentService;

    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        departmentRepository = mock(DepartmentRepository.class);
        departmentService = new DepartmentServiceImpl(departmentRepository);
    }

    @Test
    void findAll() {
        Department dep1 = new Department(1L, "department1");
        Department dep2 = new Department(2L, "department2");
        Position position = new Position(1L, "position");
        dep1.setPositions(Set.of(position));
        Page<Department> departments = new PageImpl<>(List.of(dep1, dep2));
        Pageable pageable = PageRequest.of(0, 20);

        when(departmentRepository.findAll(pageable)).thenReturn(departments);

        Page<DepartmentResponse> response = departmentService.findAll(pageable);
        assertEquals(dep1.getId(), response.getContent().get(0).getId());
        assertEquals(dep1.getName(), response.getContent().get(0).getName());
        assertTrue(response.getContent().get(0).getPositions()
                .contains(PositionResponse.fromPositionWithBasicAttributes(position)));
        assertEquals(response.getContent().get(0).getPositions().size(), 1);

        assertEquals(dep2.getId(), response.getContent().get(1).getId());
        assertEquals(dep2.getName(), response.getContent().get(1).getName());
        assertNull(response.getContent().get(1).getPositions());
        verify(departmentRepository).findAll(pageable);

        verifyNoMoreInteractions(departmentRepository);
    }

    @Test
    void create() {
        SaveDepartmentRequest request = new SaveDepartmentRequest("department");
        SaveDepartmentRequest duplicated = new SaveDepartmentRequest("departmentDup");
        long id = 1L;

        when(departmentRepository.existsByName(duplicated.getName())).thenReturn(true);
        when(departmentRepository.existsByName(request.getName())).thenReturn(false);
        when(departmentRepository.save(notNull())).thenAnswer(invocation -> {
            Department department = invocation.getArgument(0);
            assertNull(department.getId());
            assertEquals(request.getName(), department.getName());
            department.setId(id);
            return department;
        });

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> departmentService.create(duplicated))
                .satisfies(e -> assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()));
        verify(departmentRepository).existsByName(duplicated.getName());


        DepartmentResponse response = departmentService.create(request);

        assertEquals(id, response.getId());
        assertEquals(request.getName(), response.getName());
        verify(departmentRepository).existsByName(request.getName());
        verify(departmentRepository).save(notNull());
        verifyNoMoreInteractions(departmentRepository);
    }

    @Test
    void update() {
        long absentId = 10;
        long presentId = 1;
        SaveDepartmentRequest request = new SaveDepartmentRequest("new department");
        SaveDepartmentRequest duplicated = new SaveDepartmentRequest("duplicated");
        Department department = new Department(presentId, "department");

        when(departmentRepository.findById(absentId)).thenReturn(Optional.empty());
        when(departmentRepository.findById(presentId)).thenReturn(Optional.of(department));
        when(departmentRepository.existsByName(duplicated.getName())).thenReturn(true);
        when(departmentRepository.existsByName(request.getName())).thenReturn(false);
        when(departmentRepository.save(same(department))).thenReturn(department);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> departmentService.update(absentId, request))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(departmentRepository).findById(absentId);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> departmentService.update(presentId, duplicated))
                .satisfies(e -> assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()));
        verify(departmentRepository).findById(presentId);
        verify(departmentRepository).existsByName(duplicated.getName());


        departmentService.update(presentId, request);

        assertEquals(request.getName(), department.getName());

        verify(departmentRepository).existsByName(request.getName());
        verify(departmentRepository, times(2)).findById(presentId);
        verify(departmentRepository).save(same(department));

        verifyNoMoreInteractions(departmentRepository);
    }

    @Test
    void deleteById() {
        long absentId = 10;
        long presentId = 1;
        Department department = new Department(presentId, "department");

        when(departmentRepository.findById(absentId)).thenReturn(Optional.empty());
        when(departmentRepository.findById(presentId)).thenReturn(Optional.of(department));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> departmentService.deleteById(absentId))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(departmentRepository).findById(absentId);

        departmentService.deleteById(presentId);
        verify(departmentRepository).findById(presentId);
        verify(departmentRepository).deleteById(presentId);

        verifyNoMoreInteractions(departmentRepository);
    }

    @Test
    void getById() {
        long absentId = 10;
        long presentId = 1;
        Department department = new Department(presentId, "department");
        Position position = new Position(1L, "position");
        department.setPositions(Set.of(position));

        when(departmentRepository.findById(absentId)).thenReturn(Optional.empty());
        when(departmentRepository.findById(presentId)).thenReturn(Optional.of(department));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> departmentService.getById(absentId))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(departmentRepository).findById(absentId);

        DepartmentResponse response = departmentService.getById(presentId);
        assertEquals(department.getId(), response.getId());
        assertEquals(department.getName(), response.getName());
        assertTrue(response.getPositions().contains(PositionResponse.fromPositionWithBasicAttributes(position)));
        assertEquals(response.getPositions().size(), 1);
        verify(departmentRepository).findById(presentId);

        verifyNoMoreInteractions(departmentRepository);
    }
}