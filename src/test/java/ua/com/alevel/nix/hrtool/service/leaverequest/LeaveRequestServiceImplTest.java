package ua.com.alevel.nix.hrtool.service.leaverequest;

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
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestStatus;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestType;
import ua.com.alevel.nix.hrtool.model.leaverequest.request.SaveLeaveRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.response.LeaveRequestResponse;
import ua.com.alevel.nix.hrtool.repository.EmployeeRepository;
import ua.com.alevel.nix.hrtool.repository.LeaveRequestRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeaveRequestServiceImplTest {

    private EmployeeRepository employeeRepository;

    private LeaveRequestRepository leaveRequestRepository;

    private LeaveRequestServiceImpl leaveRequestService;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        leaveRequestRepository = mock(LeaveRequestRepository.class);
        leaveRequestService = new LeaveRequestServiceImpl(employeeRepository, leaveRequestRepository);
    }

    @Test
    void findAll() {
        testUnExistingEmployeeOnFind();
        testValidDataOnFind();

        verifyNoMoreInteractions(employeeRepository, leaveRequestRepository);
    }

    private void testUnExistingEmployeeOnFind() {
        String employeeEmail = "invalid@email.com";
        Pageable pageable = PageRequest.of(0, 20);

        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.findAll(pageable, employeeEmail))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(employeeRepository).findByEmail(employeeEmail);
    }

    private void testValidDataOnFind() {
        Employee employee = new Employee(
                1L, "valid@email.com", new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        LeaveRequest leaveRequest1 = new LeaveRequest(
                1L, LeaveRequestType.PAYABLE, LeaveRequestStatus.PENDING, LocalDate.now(), LocalDate.now(), null);
        LeaveRequest leaveRequest2 = new LeaveRequest(
                2L, LeaveRequestType.SICK_LEAVE, LeaveRequestStatus.APPROVED,
                LocalDate.now().minusDays(1), LocalDate.now().minusDays(1), null);
        leaveRequest1.setEmployee(employee);
        leaveRequest2.setEmployee(employee);
        Page<LeaveRequest> leaveRequestPage = new PageImpl<>(List.of(leaveRequest1, leaveRequest2));
        Pageable pageable = PageRequest.of(0, 20);

        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findAllByEmployeeOrderByIdDesc(pageable, employee)).thenReturn(leaveRequestPage);

        Page<LeaveRequestResponse> responses = leaveRequestService.findAll(pageable, employee.getEmail());

        assertEquals(leaveRequestPage.getTotalElements(), responses.getTotalElements());
        assertTrue(
                responses.getContent().containsAll(
                        leaveRequestPage.getContent().stream()
                                .map(LeaveRequestResponse::fromLeaveRequestWithBasicAttributes)
                                .collect(Collectors.toList())
                )
        );
        verify(employeeRepository).findByEmail(employee.getEmail());
        verify(leaveRequestRepository).findAllByEmployeeOrderByIdDesc(pageable, employee);
    }

    @Test
    void findAllByStatus() {
        testInvalidStatusOnFindByStatus();
        testValidDataOnFindByStatus();
    }

    private void testInvalidStatusOnFindByStatus() {
        String status = "invalid";
        Pageable pageable = PageRequest.of(0, 20);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.findAllByStatus(pageable, status))
                .satisfies(e -> assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()));
    }

    private void testValidDataOnFindByStatus() {
        Employee employee1 = new Employee(
                1L, "valid@email.com", new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        Employee employee2 = new Employee(
                2L, "valid2@email.com", new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        LeaveRequest leaveRequest1 = new LeaveRequest(
                1L, LeaveRequestType.PAYABLE, LeaveRequestStatus.PENDING, LocalDate.now(), LocalDate.now(), null);
        LeaveRequest leaveRequest2 = new LeaveRequest(
                2L, LeaveRequestType.SICK_LEAVE, LeaveRequestStatus.APPROVED, LocalDate.now(), LocalDate.now(), null);
        leaveRequest1.setEmployee(employee1);
        leaveRequest2.setEmployee(employee2);
        Page<LeaveRequest> leaveRequestPage = new PageImpl<>(List.of(leaveRequest2));
        Pageable pageable = PageRequest.of(0, 20);
        LeaveRequestStatus status = LeaveRequestStatus.APPROVED;

        when(leaveRequestRepository.findAllByStatus(pageable, status)).thenReturn(leaveRequestPage);

        Page<LeaveRequestResponse> responses = leaveRequestService.findAllByStatus(pageable, status.toString());

        assertEquals(1, responses.getTotalElements());
        assertTrue(
                responses.getContent().contains(LeaveRequestResponse.fromLeaveRequest(leaveRequest2))
        );
        verify(leaveRequestRepository).findAllByStatus(pageable, status);
    }

    @Test
    void create() {
        testUnExistingEmployeeOnCreate();
        testInvalidDatesOnCreate();
        testValidDataOnCreate();

        verifyNoMoreInteractions(employeeRepository, leaveRequestRepository);
    }

    private void testUnExistingEmployeeOnCreate() {
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.PAYABLE.toString(), LocalDate.now(), LocalDate.now(), null);
        String employeeEmail = "invalid@email.com";

        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.create(request, employeeEmail))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(employeeRepository).findByEmail(employeeEmail);
    }

    private void testInvalidDatesOnCreate() {
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.PAYABLE.toString(), LocalDate.now(), LocalDate.now(), null);
        String employeeEmail = "invalid@dates.com";
        Employee employee = new Employee(10L, "valid@email.com", new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        LeaveRequest leaveRequest = new LeaveRequest(
                LeaveRequestType.PAYABLE, LeaveRequestStatus.APPROVED, LocalDate.now().minusDays(1), LocalDate.now(), null);
        List<LeaveRequestStatus> statusList = List.of(LeaveRequestStatus.PENDING, LeaveRequestStatus.APPROVED);


        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.requestsForPeriod(employee, request.getStart(), request.getEnd(), statusList))
                .thenReturn(List.of(leaveRequest));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.create(request, employeeEmail))
                .satisfies(e -> assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()));
        verify(employeeRepository).findByEmail(employeeEmail);
        verify(leaveRequestRepository).requestsForPeriod(employee, request.getStart(), request.getEnd(), statusList);
    }

    private void testValidDataOnCreate() {
        long requestId = 1;
        Employee employee = new Employee(1L, "valid@email.com", new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        SaveLeaveRequest request = new SaveLeaveRequest(LeaveRequestType.PAYABLE.toString(), LocalDate.now(), LocalDate.now(), null);
        List<LeaveRequestStatus> statusList = List.of(LeaveRequestStatus.PENDING, LeaveRequestStatus.APPROVED);

        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.requestsForPeriod(employee, request.getStart(), request.getEnd(), statusList))
                .thenReturn(List.of());

        when(leaveRequestRepository.save(notNull())).thenAnswer(invocation -> {
            LeaveRequest leaveRequest = invocation.getArgument(0);
            assertNull(leaveRequest.getId());
            assertEquals(request.getType(), leaveRequest.getType().toString());
            assertEquals(request.getStart(), leaveRequest.getStart());
            assertEquals(request.getEnd(), leaveRequest.getEnd());
            assertEquals(request.getComment(), leaveRequest.getComment());
            assertEquals(LeaveRequestStatus.PENDING, leaveRequest.getStatus());
            assertEquals(employee, leaveRequest.getEmployee());
            leaveRequest.setId(requestId);
            return leaveRequest;
        });

        LeaveRequestResponse response = leaveRequestService.create(request, employee.getEmail());
        assertEquals(requestId, response.getId());
        assertEquals(request.getType(), response.getType().toString());
        assertEquals(LeaveRequestStatus.PENDING, response.getStatus());
        assertEquals(request.getStart(), response.getStart());
        assertEquals(request.getEnd(), response.getEnd());
        assertEquals(request.getComment(), response.getComment());
        assertNull(response.getEmployee());
        verify(employeeRepository).findByEmail(employee.getEmail());
        verify(leaveRequestRepository).requestsForPeriod(employee, request.getStart(), request.getEnd(), statusList);
        verify(leaveRequestRepository).save(notNull());
    }

    @Test
    void update() {
        testUnExistingEmployeeOnUpdate();
        testUnExistingRequestOnUpdate();
        testForbiddenActionOnUpdate();
        testInvalidDataOnUpdate();
        testValidDataOnUpdate();

        verifyNoMoreInteractions(employeeRepository, leaveRequestRepository);
    }

    private void testUnExistingEmployeeOnUpdate() {
        long requestId = 1;
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.PAYABLE.toString(), LocalDate.now(), LocalDate.now(), null);
        String employeeEmail = "invalid@email.com";

        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.update(requestId, request, employeeEmail))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(employeeRepository).findByEmail(employeeEmail);
    }

    private void testUnExistingRequestOnUpdate() {
        long absentId = 10;
        String employeeEmail = "invalid@request.com";
        Employee employee = new Employee(1L, employeeEmail, new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.PAYABLE.toString(), LocalDate.now(), LocalDate.now(), null);
        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findById(absentId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.update(absentId, request, employeeEmail))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(employeeRepository).findByEmail(employeeEmail);
        verify(leaveRequestRepository).findById(absentId);
    }

    private void testForbiddenActionOnUpdate() {
        long presentId = 3;
        String employeeEmail = "invalid@action.com";
        Employee employee = new Employee(1L, employeeEmail, new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        LeaveRequest leaveRequest = new LeaveRequest(
                1L, LeaveRequestType.PAYABLE, LeaveRequestStatus.APPROVED, LocalDate.now(), LocalDate.now(), null);
        leaveRequest.setEmployee(employee);
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.PAYABLE.toString(), LocalDate.now(), LocalDate.now(), null);

        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findById(presentId)).thenReturn(Optional.of(leaveRequest));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.update(presentId, request, employeeEmail))
                .satisfies(e -> assertEquals(HttpStatus.FORBIDDEN, e.getStatus()));
        verify(employeeRepository).findByEmail(employeeEmail);
        verify(leaveRequestRepository).findById(presentId);
    }

    private void testInvalidDataOnUpdate() {
        long requestId = 2;
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.PAYABLE.toString(), LocalDate.now(), LocalDate.now(), null);
        String employeeEmail = "invalid@dates.com";
        Employee employee = new Employee(10L, employeeEmail, new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        LeaveRequest leaveRequestToUpdate = new LeaveRequest(
                requestId, LeaveRequestType.PAYABLE, LeaveRequestStatus.PENDING, LocalDate.now().minusDays(1), LocalDate.now(), null);
        leaveRequestToUpdate.setEmployee(employee);
        LeaveRequest leaveRequestExisting = new LeaveRequest(
                LeaveRequestType.PAYABLE, LeaveRequestStatus.APPROVED, LocalDate.now().minusDays(1), LocalDate.now(), null);
        List<LeaveRequestStatus> statusList = List.of(LeaveRequestStatus.PENDING, LeaveRequestStatus.APPROVED);

        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findById(requestId)).thenReturn(Optional.of(leaveRequestToUpdate));
        when(leaveRequestRepository.requestsForPeriod(employee, request.getStart(), request.getEnd(), statusList, requestId))
                .thenReturn(List.of(leaveRequestExisting));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.update(requestId, request, employeeEmail))
                .satisfies(e -> assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()));
        verify(employeeRepository).findByEmail(employeeEmail);
        verify(leaveRequestRepository).findById(requestId);
        verify(leaveRequestRepository).requestsForPeriod(employee, request.getStart(), request.getEnd(), statusList, requestId);
    }

    private void testValidDataOnUpdate() {
        long leaveRequestId = 1L;
        String employeeEmail = "valid@email.com";
        Employee employee = new Employee(1L, employeeEmail, new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        LeaveRequest leaveRequest = new LeaveRequest(
                leaveRequestId, LeaveRequestType.PAYABLE, LeaveRequestStatus.PENDING, LocalDate.now(), LocalDate.now(), null);
        leaveRequest.setEmployee(employee);
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.SICK_LEAVE.toString(), LocalDate.now(), LocalDate.now(), "Updated");
        List<LeaveRequestStatus> statusList = List.of(LeaveRequestStatus.PENDING, LeaveRequestStatus.APPROVED);

        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findById(leaveRequestId)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.requestsForPeriod(employee, request.getStart(), request.getEnd(), statusList, leaveRequestId))
                .thenReturn(List.of());

        leaveRequestService.update(leaveRequestId, request, employeeEmail);

        assertEquals(request.getType(), leaveRequest.getType().toString());
        assertEquals(request.getStart(), leaveRequest.getStart());
        assertEquals(request.getEnd(), leaveRequest.getEnd());
        assertEquals(request.getComment(), leaveRequest.getComment());

        verify(employeeRepository).findByEmail(employeeEmail);
        verify(leaveRequestRepository).findById(leaveRequestId);
        verify(leaveRequestRepository).requestsForPeriod(employee, request.getStart(), request.getEnd(), statusList, leaveRequestId);
        verify(leaveRequestRepository).save(leaveRequest);
    }

    @Test
    void deleteById() {
        testUnExistingEmployeeOnDelete();
        testUnExistingRequestOnDelete();
        testForbiddenActionOnDelete();
        testValidDataOnDelete();

        verifyNoMoreInteractions(leaveRequestRepository, employeeRepository);
    }

    private void testUnExistingEmployeeOnDelete() {
        String employeeEmail = "invalid@email.com";

        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.deleteById(1, employeeEmail))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(employeeRepository).findByEmail(employeeEmail);
    }

    private void testUnExistingRequestOnDelete() {
        long absentId = 10;
        String employeeEmail = "invalid@request.com";
        Employee employee = new Employee(1L, employeeEmail, new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());

        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findById(absentId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.deleteById(absentId, employeeEmail))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(employeeRepository).findByEmail(employeeEmail);
        verify(leaveRequestRepository).findById(absentId);
    }

    private void testForbiddenActionOnDelete() {
        long presentId = 2;
        String employeeEmail = "invalid@action.com";
        Employee employee = new Employee(1L, employeeEmail, new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        LeaveRequest leaveRequest = new LeaveRequest(
                1L, LeaveRequestType.PAYABLE, LeaveRequestStatus.APPROVED, LocalDate.now(), LocalDate.now(), null);
        leaveRequest.setEmployee(employee);

        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findById(presentId)).thenReturn(Optional.of(leaveRequest));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.deleteById(presentId, employeeEmail))
                .satisfies(e -> assertEquals(HttpStatus.FORBIDDEN, e.getStatus()));
        verify(employeeRepository).findByEmail(employeeEmail);
        verify(leaveRequestRepository).findById(presentId);
    }

    private void testValidDataOnDelete() {
        long presentId = 1;
        String employeeEmail = "valid@email.com";
        Employee employee = new Employee(1L, employeeEmail, new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        LeaveRequest leaveRequest = new LeaveRequest(
                1L, LeaveRequestType.PAYABLE, LeaveRequestStatus.PENDING, LocalDate.now(), LocalDate.now(), null);
        leaveRequest.setEmployee(employee);

        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findById(presentId)).thenReturn(Optional.of(leaveRequest));

        leaveRequestService.deleteById(presentId, employeeEmail);
        verify(employeeRepository).findByEmail(employeeEmail);
        verify(leaveRequestRepository).findById(presentId);
        verify(leaveRequestRepository).deleteById(presentId);
    }

    @Test
    void approve() {
        long absentId = 10;
        long presentId = 1;
        LeaveRequest leaveRequest = new LeaveRequest(
                1L, LeaveRequestType.PAYABLE, LeaveRequestStatus.PENDING, LocalDate.now(), LocalDate.now(), null);

        when(leaveRequestRepository.findById(absentId)).thenReturn(Optional.empty());
        when(leaveRequestRepository.findById(presentId)).thenReturn(Optional.of(leaveRequest));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.approve(absentId))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(leaveRequestRepository).findById(absentId);

        leaveRequestService.approve(presentId);
        verify(leaveRequestRepository).findById(presentId);
        verify(leaveRequestRepository).save(leaveRequest);

        verifyNoMoreInteractions(leaveRequestRepository);
    }

    @Test
    void decline() {
        long absentId = 10;
        long presentId = 1;
        LeaveRequest leaveRequest = new LeaveRequest(
                1L, LeaveRequestType.PAYABLE, LeaveRequestStatus.PENDING, LocalDate.now(), LocalDate.now(), null);

        when(leaveRequestRepository.findById(absentId)).thenReturn(Optional.empty());
        when(leaveRequestRepository.findById(presentId)).thenReturn(Optional.of(leaveRequest));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> leaveRequestService.decline(absentId))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(leaveRequestRepository).findById(absentId);

        leaveRequestService.decline(presentId);
        verify(leaveRequestRepository).findById(presentId);
        verify(leaveRequestRepository).save(leaveRequest);

        verifyNoMoreInteractions(leaveRequestRepository);
    }
}