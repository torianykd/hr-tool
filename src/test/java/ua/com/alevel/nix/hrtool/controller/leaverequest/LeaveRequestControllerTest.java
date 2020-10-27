package ua.com.alevel.nix.hrtool.controller.leaverequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import ua.com.alevel.nix.hrtool.Routes;
import ua.com.alevel.nix.hrtool.exception.employee.EmployeeException;
import ua.com.alevel.nix.hrtool.exception.leaverequest.LeaveRequestException;
import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestStatus;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestType;
import ua.com.alevel.nix.hrtool.model.leaverequest.request.SaveLeaveRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.response.LeaveRequestResponse;
import ua.com.alevel.nix.hrtool.service.leaverequest.LeaveRequestService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LeaveRequestControllerTest {

    private MockMvc mvc;

    private LeaveRequestService leaveRequestService;

    Principal principal;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        leaveRequestService = mock(LeaveRequestService.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new LeaveRequestController(leaveRequestService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void testListRequests() throws Exception {
        EmployeeResponse employeeResponse = new EmployeeResponse(
                1L, "employee@email.com", "first", "last", LocalDate.now(), LocalDate.now());
        LeaveRequestResponse response1 = new LeaveRequestResponse(
                1L, LeaveRequestType.PAYABLE, LeaveRequestStatus.PENDING, LocalDate.now(), LocalDate.now(), null);
        response1.setEmployee(employeeResponse);
        EmployeeResponse employee2Response = new EmployeeResponse(
                2L, "employee@email.com", "first", "last", LocalDate.now(), LocalDate.now());
        LeaveRequestResponse response2 = new LeaveRequestResponse(
                2L, LeaveRequestType.PAYABLE, LeaveRequestStatus.PENDING, LocalDate.now(), LocalDate.now(), null);
        response2.setEmployee(employee2Response);
        Pageable pageable = PageRequest.of(0, 20);
        Page<LeaveRequestResponse> responsePage = new PageImpl<>(List.of(response1));
        String employeeEmail = employeeResponse.getEmail();

        when(principal.getName())
                .thenReturn(employeeEmail);
        when(leaveRequestService.findAll(pageable, employeeEmail))
                .thenReturn(responsePage);

        mvc.perform(get(Routes.LEAVE_REQUESTS)
                .param("page", "0").param("size", "20")
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(buildJsonStringFromResponse(response1))))
                .andExpect(content().string(not(containsString(buildJsonStringFromResponse(response2)))));

        verify(leaveRequestService).findAll(pageable, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testInvalidEmployeeOnListRequests() throws Exception {
        String employeeEmail = "invalid@email.com";
        Pageable pageable = PageRequest.of(0, 20);

        when(principal.getName())
                .thenReturn(employeeEmail);
        when(leaveRequestService.findAll(pageable, employeeEmail))
                .thenThrow(EmployeeException.employeeNotFound(employeeEmail));

        mvc.perform(get(Routes.LEAVE_REQUESTS)
                .principal(principal))
                .andExpect(status().isNotFound());

        verify(leaveRequestService).findAll(pageable, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testCreateLeaveRequest() throws Exception {
        SaveLeaveRequest validRequest = new SaveLeaveRequest("payable", LocalDate.now(), LocalDate.now(), null);
        String employeeEmail = "invalid@email.com";
        LeaveRequestResponse response = new LeaveRequestResponse(
                1L, LeaveRequestType.PAYABLE, LeaveRequestStatus.DECLINED, LocalDate.now(), LocalDate.now(), null);

        when(principal.getName())
                .thenReturn(employeeEmail);
        when(leaveRequestService.create(validRequest, employeeEmail))
                .thenReturn(response);

        mvc.perform(post(Routes.LEAVE_REQUESTS)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(buildJsonStringFromResponse(response)));

        verify(leaveRequestService).create(validRequest, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testSaveRequestConstraints() throws Exception {
        SaveLeaveRequest invalidTypeRequest = new SaveLeaveRequest("random", LocalDate.now(), LocalDate.now(), null);
        SaveLeaveRequest invalidStartDateRequest = new SaveLeaveRequest("random", LocalDate.now().minusDays(5), LocalDate.now(), null);
        SaveLeaveRequest invalidEndDateRequest = new SaveLeaveRequest("random", LocalDate.now(), LocalDate.now().minusDays(5), null);

        List<SaveLeaveRequest> requests = List.of(invalidTypeRequest, invalidStartDateRequest, invalidEndDateRequest);
        requests.forEach(request ->
                when(leaveRequestService.create(request, principal.getName()))
                        .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
        );

        for (SaveLeaveRequest request : requests) {
            mvc.perform(post(Routes.LEAVE_REQUESTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(buildJsonStringFromRequest(request)))
                    .andExpect(status().isBadRequest());
        }

        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testInvalidEmployeeOnRequestCreate() throws Exception {
        SaveLeaveRequest validRequest = new SaveLeaveRequest("payable", LocalDate.now(), LocalDate.now(), null);
        String employeeEmail = "invalid@email.com";

        when(principal.getName())
                .thenReturn(employeeEmail);
        when(leaveRequestService.create(validRequest, employeeEmail))
                .thenThrow(EmployeeException.employeeNotFound(employeeEmail));

        mvc.perform(post(Routes.LEAVE_REQUESTS)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(validRequest)))
                .andExpect(status().isNotFound());

        verify(leaveRequestService).create(validRequest, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testInvalidDatesOnRequestCreate() throws Exception {
        SaveLeaveRequest validRequest = new SaveLeaveRequest("payable", LocalDate.now(), LocalDate.now(), null);
        String employeeEmail = "invalid@email.com";

        when(principal.getName())
                .thenReturn(employeeEmail);
        when(leaveRequestService.create(validRequest, employeeEmail))
                .thenThrow(LeaveRequestException.requestAlreadyExists());

        mvc.perform(post(Routes.LEAVE_REQUESTS)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(validRequest)))
                .andExpect(status().isBadRequest());

        verify(leaveRequestService).create(validRequest, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);


    }

    @Test
    void testUpdateRequest() throws Exception {
        long presentId = 1;
        String employeeEmail = "employee@email.com";
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.SICK_LEAVE.toString(), LocalDate.now(), LocalDate.now(), null);

        when(principal.getName())
                .thenReturn(employeeEmail);
        doNothing()
                .when(leaveRequestService)
                .update(presentId, request, employeeEmail);

        mvc.perform(put(Routes.LEAVE_REQUESTS + "/" + presentId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(request)))
                .andExpect(status().isNoContent());

        verify(leaveRequestService).update(presentId, request, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testInvalidEmployeeOnUpdate() throws Exception {
        long presentId = 1;
        String employeeEmail = "invalid@email.com";
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.SICK_LEAVE.toString(), LocalDate.now(), LocalDate.now(), null);

        when(principal.getName())
                .thenReturn(employeeEmail);
        doThrow(EmployeeException.employeeNotFound(employeeEmail))
                .when(leaveRequestService)
                .update(presentId, request, employeeEmail);

        mvc.perform(put(Routes.LEAVE_REQUESTS + "/" + presentId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(request)))
                .andExpect(status().isNotFound());

        verify(leaveRequestService).update(presentId, request, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testInvalidRequestOnUpdate() throws Exception {
        long absentId = 10;
        String employeeEmail = "valid@email.com";
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.SICK_LEAVE.toString(), LocalDate.now(), LocalDate.now(), null);

        when(principal.getName())
                .thenReturn(employeeEmail);
        doThrow(LeaveRequestException.requestNotFound(absentId))
                .when(leaveRequestService)
                .update(absentId, request, employeeEmail);

        mvc.perform(put(Routes.LEAVE_REQUESTS + "/" + absentId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(request)))
                .andExpect(status().isNotFound());

        verify(leaveRequestService).update(absentId, request, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testForbiddenActionOmUpdate() throws Exception {
        long presentId = 1;
        String employeeEmail = "valid@email.com";
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.SICK_LEAVE.toString(), LocalDate.now(), LocalDate.now(), null);

        when(principal.getName())
                .thenReturn(employeeEmail);
        doThrow(LeaveRequestException.actionForbidden())
                .when(leaveRequestService)
                .update(presentId, request, employeeEmail);

        mvc.perform(put(Routes.LEAVE_REQUESTS + "/" + presentId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(request)))
                .andExpect(status().isForbidden());

        verify(leaveRequestService).update(presentId, request, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testInvalidDatesOnUpdate() throws Exception {
        long presentId = 1;
        String employeeEmail = "valid@email.com";
        SaveLeaveRequest request = new SaveLeaveRequest(
                LeaveRequestType.SICK_LEAVE.toString(), LocalDate.now(), LocalDate.now(), null);

        when(principal.getName())
                .thenReturn(employeeEmail);
        doThrow(LeaveRequestException.requestAlreadyExists())
                .when(leaveRequestService)
                .update(presentId, request, employeeEmail);

        mvc.perform(put(Routes.LEAVE_REQUESTS + "/" + presentId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(request)))
                .andExpect(status().isBadRequest());

        verify(leaveRequestService).update(presentId, request, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testDeleteLeaveRequest() throws Exception {
        long presentId = 1;
        String employeeEmail = "invalid@email.com";

        when(principal.getName())
                .thenReturn(employeeEmail);
        doNothing()
                .when(leaveRequestService)
                .deleteById(presentId, employeeEmail);

        mvc.perform(delete(Routes.LEAVE_REQUESTS + "/" + presentId)
                .principal(principal))
                .andExpect(status().isNoContent());

        verify(leaveRequestService).deleteById(presentId, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testInvalidEmployeeOnDeleteLeaveRequest() throws Exception {
        long presentId = 1;
        String employeeEmail = "invalid@email.com";

        when(principal.getName())
                .thenReturn(employeeEmail);
        doThrow(EmployeeException.employeeNotFound(employeeEmail))
                .when(leaveRequestService)
                .deleteById(presentId, employeeEmail);

        mvc.perform(delete(Routes.LEAVE_REQUESTS + "/" + presentId)
                .principal(principal))
                .andExpect(status().isNotFound());

        verify(leaveRequestService).deleteById(presentId, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testInvalidRequestOnDeleteLeaveRequest() throws Exception {
        long absentId = 10;
        String employeeEmail = "invalid@email.com";

        when(principal.getName())
                .thenReturn(employeeEmail);
        doThrow(LeaveRequestException.requestNotFound(absentId))
                .when(leaveRequestService)
                .deleteById(absentId, employeeEmail);

        mvc.perform(delete(Routes.LEAVE_REQUESTS + "/" + absentId)
                .principal(principal))
                .andExpect(status().isNotFound());

        verify(leaveRequestService).deleteById(absentId, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testForbiddenActionOnDeleteLeaveRequest() throws Exception {
        long presentId = 1;
        String employeeEmail = "valid@email.com";

        when(principal.getName())
                .thenReturn(employeeEmail);
        doThrow(LeaveRequestException.actionForbidden())
                .when(leaveRequestService)
                .deleteById(presentId, employeeEmail);

        mvc.perform(delete(Routes.LEAVE_REQUESTS + "/" + presentId)
                .principal(principal))
                .andExpect(status().isForbidden());

        verify(leaveRequestService).deleteById(presentId, employeeEmail);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testApproveRequest() throws Exception {
        long presentId = 1;

        doNothing()
                .when(leaveRequestService)
                .approve(presentId);

        mvc.perform(post(Routes.LEAVE_REQUESTS + "/" + presentId + "/approve"))
                .andExpect(status().isNoContent());

        verify(leaveRequestService).approve(presentId);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testInvalidRequestOnApprove() throws Exception {
        long absentId = 1;

        doThrow(LeaveRequestException.requestNotFound(absentId))
                .when(leaveRequestService)
                .approve(absentId);

        mvc.perform(post(Routes.LEAVE_REQUESTS + "/" + absentId + "/approve"))
                .andExpect(status().isNotFound());

        verify(leaveRequestService).approve(absentId);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testDeclineRequest() throws Exception {
        long presentId = 1;

        doNothing()
                .when(leaveRequestService)
                .approve(presentId);

        mvc.perform(post(Routes.LEAVE_REQUESTS + "/" + presentId + "/decline"))
                .andExpect(status().isNoContent());

        verify(leaveRequestService).decline(presentId);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testInvalidRequestOnDecline() throws Exception {
        long absentId = 1;

        doThrow(LeaveRequestException.requestNotFound(absentId))
                .when(leaveRequestService)
                .decline(absentId);

        mvc.perform(post(Routes.LEAVE_REQUESTS + "/" + absentId + "/decline"))
                .andExpect(status().isNotFound());

        verify(leaveRequestService).decline(absentId);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testManageRequests() throws Exception {
        String status = LeaveRequestStatus.PENDING.toString();
        EmployeeResponse employeeResponse = new EmployeeResponse(
                1L, "employee@email.com", "first", "last", LocalDate.now(), LocalDate.now());
        LeaveRequestResponse response = new LeaveRequestResponse(
                1L, LeaveRequestType.PAYABLE, LeaveRequestStatus.PENDING, LocalDate.now(), LocalDate.now(), null);
        response.setEmployee(employeeResponse);
        LeaveRequestResponse notInResponse = new LeaveRequestResponse(
                1L, LeaveRequestType.PAYABLE, LeaveRequestStatus.APPROVED, LocalDate.now(), LocalDate.now(), null);
        Pageable pageable = PageRequest.of(0, 20);
        Page<LeaveRequestResponse> responsePage = new PageImpl<>(List.of(response));

        when(leaveRequestService.findAllByStatus(pageable, status))
                .thenReturn(responsePage);

        mvc.perform(get(Routes.LEAVE_REQUESTS + "/manage")
                .param("page", "0").param("size", "20").param("status", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(buildJsonStringFromResponse(response))))
                .andExpect(content().string(not(containsString(buildJsonStringFromResponse(notInResponse)))));

        verify(leaveRequestService).findAllByStatus(pageable, status);
        verifyNoMoreInteractions(leaveRequestService);
    }

    @Test
    void testInvalidStatusOnManageRequest() throws Exception {
        String invalidStatus = "random";
        Pageable pageable = PageRequest.of(0, 20);

        when(leaveRequestService.findAllByStatus(pageable, invalidStatus))
                .thenThrow(LeaveRequestException.invalidStatus(invalidStatus));

        mvc.perform(get(Routes.LEAVE_REQUESTS + "/manage")
                .param("page", "0").param("size", "20").param("status", invalidStatus))
                .andExpect(status().isBadRequest());

        verify(leaveRequestService).findAllByStatus(pageable, invalidStatus);
        verifyNoMoreInteractions(leaveRequestService);
    }

    private String buildJsonStringFromRequest(SaveLeaveRequest request) {
        String comment = request.getComment() == null
                ? null
                : "\"" + request.getComment() + "\"";
        return "{" +
                    "\"type\":\"" + request.getType().toLowerCase() + "\"," +
                    "\"start\":\"" + request.getStart() + "\"," +
                    "\"end\":\"" + request.getEnd() + "\"," +
                    "\"comment\":" + comment +
                "}";
    }

    private String buildJsonStringFromResponse(LeaveRequestResponse response) {
        String comment = Optional.ofNullable(response.getComment())
                .map(c -> "\"" + c + "\"")
                .orElse(null);
        String employee = Optional.ofNullable(response.getEmployee())
                .map(e -> "{" +
                        "\"id\":" + e.getId() + "," +
                        "\"email\":\"" + e.getEmail() + "\"," +
                        "\"firstName\":\"" + e.getFirstName() + "\"," +
                        "\"lastName\":\"" + e.getLastName() + "\"," +
                        "\"birthDate\":\"" + e.getBirthDate() + "\"," +
                        "\"hiringDate\":\"" + e.getHiringDate() + "\"," +
                        "\"positions\":null," +
                        "\"contacts\":null" +
                        "}"
                ).orElse(null);
        return "{" +
                    "\"id\":" + response.getId() + "," +
                    "\"type\":\"" + response.getType() + "\"," +
                    "\"status\":\"" + response.getStatus() + "\"," +
                    "\"start\":\"" + response.getStart() + "\"," +
                    "\"end\":\"" + response.getEnd() + "\"," +
                    "\"comment\":" + comment + ","+
                    "\"employee\":" + employee +
                "}";
    }

}