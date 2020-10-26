package ua.com.alevel.nix.hrtool.controller.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.alevel.nix.hrtool.Routes;
import ua.com.alevel.nix.hrtool.exception.employee.EmployeeException;
import ua.com.alevel.nix.hrtool.model.department.response.DepartmentResponse;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveEmployeeRequest;
import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;
import ua.com.alevel.nix.hrtool.service.employee.EmployeeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeControllerTest {

    private MockMvc mvc;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = mock(EmployeeService.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new EmployeeController(employeeService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void testListEmployees() throws Exception {
        EmployeeResponse response = new EmployeeResponse(
                1L, "valid@mail.com", "first", "last", LocalDate.now(), LocalDate.now()
        );
        response.setPositions(Set.of());
        response.setContacts(Set.of());
        Pageable pageable = PageRequest.of(0, 20);
        Page<EmployeeResponse> pageResponse = new PageImpl<>(List.of(response));

        String expectedJson = buildJsonStringFromResponse(response);

        when(employeeService.findAll(pageable))
                .thenReturn(pageResponse);

        mvc.perform(get(Routes.EMPLOYEES))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(expectedJson)));

        verify(employeeService).findAll(pageable);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void testGetEmployee() throws Exception {
        long presentId = 1;
        EmployeeResponse response = new EmployeeResponse(
                presentId, "valid@mail.com", "first", "last", LocalDate.now(), LocalDate.now()
        );
        response.setPositions(Set.of());
        response.setContacts(Set.of());

        String expectedJson = buildJsonStringFromResponse(response);

        when(employeeService.getById(presentId))
                .thenReturn(response);

        mvc.perform(get(Routes.EMPLOYEES + "/" + presentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(employeeService).getById(presentId);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void testInvalidEmployeeIdOnGet() throws Exception {
        long absentId = 10;

        when(employeeService.getById(absentId))
                .thenThrow(EmployeeException.employeeNotFound(absentId));

        mvc.perform(get(Routes.EMPLOYEES + "/" + absentId))
                .andExpect(status().isNotFound());

        verify(employeeService).getById(absentId);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void testCreateEmployee() throws Exception {
        SaveEmployeeRequest request = new SaveEmployeeRequest(
                "valid@mail.com", "first", "last", LocalDate.now(), LocalDate.now());
        request.setPositionIds(List.of(1L));

        DepartmentResponse departmentResponse = new DepartmentResponse(1L, "department");
        departmentResponse.setPositions(Set.of());
        PositionResponse positionResponse = new PositionResponse(1L, "position");
        positionResponse.setDepartment(departmentResponse);
        EmployeeResponse response = new EmployeeResponse(
                1L, "valid@mail.com", "first", "last", LocalDate.now(), LocalDate.now());
        response.setPositions(Set.of(positionResponse));
        response.setContacts(Set.of());

        when(employeeService.create(request))
                .thenReturn(response);

        String expectedJson = buildJsonStringFromResponse(response);

        mvc.perform(post(Routes.EMPLOYEES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(employeeService).create(request);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void testDuplicateEmailOnCreateEmployee() throws Exception {
        SaveEmployeeRequest duplicateEmailRequest = new SaveEmployeeRequest(
                "duplicate@mail.com", "first", "last", LocalDate.now(), LocalDate.now());
        duplicateEmailRequest.setPositionIds(List.of());

        when(employeeService.create(duplicateEmailRequest))
                .thenThrow(EmployeeException.duplicateEmail(duplicateEmailRequest.getEmail()));

        mvc.perform(post(Routes.EMPLOYEES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(duplicateEmailRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeService).create(duplicateEmailRequest);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void testInvalidPositionsOnCreateEmployee() throws Exception {
        SaveEmployeeRequest invalidPositionsRequest = new SaveEmployeeRequest(
                "invalidPositions@mail.com", "first", "last", LocalDate.now(), LocalDate.now());
        invalidPositionsRequest.setPositionIds(List.of(100L, 1000L));

        when(employeeService.create(invalidPositionsRequest))
                .thenThrow(EmployeeException.positionsNotExist());

        mvc.perform(post(Routes.EMPLOYEES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(invalidPositionsRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeService).create(invalidPositionsRequest);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void testUpdateEmployee() throws Exception {
        long presentId = 1;
        SaveEmployeeRequest request = new SaveEmployeeRequest(
                "valid@mail.com", "first", "last", LocalDate.now(), LocalDate.now());
        request.setPositionIds(List.of(1L));

        doNothing()
                .when(employeeService)
                .update(presentId, request);

        mvc.perform(put(Routes.EMPLOYEES + "/" + presentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(request)))
                .andExpect(status().isNoContent());

        verify(employeeService).update(presentId, request);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void testInvalidEmployeeIdOnUpdateEmployee() throws Exception {
        long absentId = 10;
        SaveEmployeeRequest request = new SaveEmployeeRequest(
                "valid@emial.com", "first", "last", LocalDate.now(), LocalDate.now()
        );

        doThrow(EmployeeException.employeeNotFound(absentId))
                .when(employeeService)
                .update(absentId, request);

        mvc.perform(put(Routes.EMPLOYEES + "/" + absentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(request)))
                .andExpect(status().isNotFound());

        verify(employeeService).update(absentId, request);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void testDuplicateEmailOnUpdateEmployee() throws Exception {
        long absentId = 10;
        SaveEmployeeRequest duplicateEmailRequest = new SaveEmployeeRequest(
                "duplicate@mail.com", "first", "last", LocalDate.now(), LocalDate.now());

        doThrow(EmployeeException.duplicateEmail(duplicateEmailRequest.getEmail()))
                .when(employeeService)
                .update(absentId, duplicateEmailRequest);

        mvc.perform(put(Routes.EMPLOYEES + "/" + absentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(duplicateEmailRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeService).update(absentId, duplicateEmailRequest);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void testInvalidPositionsOnUpdateEmployee() throws Exception {
        long absentId = 10;
        SaveEmployeeRequest invalidPositionsRequest = new SaveEmployeeRequest(
                "duplicate@mail.com", "first", "last", LocalDate.now(), LocalDate.now());
        invalidPositionsRequest.setPositionIds(List.of(100L, 1000L));

        doThrow(EmployeeException.positionsNotExist())
                .when(employeeService)
                .update(absentId, invalidPositionsRequest);

        mvc.perform(put(Routes.EMPLOYEES + "/" + absentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(invalidPositionsRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeService).update(absentId, invalidPositionsRequest);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void testDeleteEmployee() throws Exception {
        long presentId = 1;

        doNothing()
                .when(employeeService)
                .deleteById(presentId);

        mvc.perform(delete(Routes.EMPLOYEES + "/" + presentId))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteById(presentId);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void testInvalidEmployeeIdOnDelete() throws Exception {
        long absentId = 10;

        doThrow(EmployeeException.employeeNotFound(absentId))
                .when(employeeService)
                .deleteById(absentId);

        mvc.perform(delete(Routes.EMPLOYEES + "/" + absentId))
                .andExpect(status().isNotFound());

        verify(employeeService).deleteById(absentId);
        verifyNoMoreInteractions(employeeService);
    }

    private String buildJsonStringFromRequest(SaveEmployeeRequest request) {
        String positionIds = Optional.ofNullable(request.getPositionIds())
                .map(positions -> positions.stream().map(Objects::toString).collect(Collectors.joining(",")))
                .orElse("");
        return "{" +
                "  \"email\": \"" + request.getEmail() + "\"," +
                "  \"firstName\": \"" + request.getFirstName() + "\"," +
                "  \"lastName\": \"" + request.getLastName() + "\"," +
                "  \"birthDate\": \"" + request.getBirthDate() + "\"," +
                "  \"hiringDate\": \"" + request.getHiringDate() + "\"," +
                "  \"positionIds\": [" + positionIds + "]" +
                "}";
    }

    private String buildJsonStringFromResponse(EmployeeResponse response) {
        String positions = Optional.ofNullable(response.getPositions())
                .map(positionResponses -> positionResponses.stream()
                        .map(positionResponse -> "{" +
                                "\"id\":" + positionResponse.getId() + "," +
                                "\"name\":\"" + positionResponse.getName() + "\"," +
                                "\"department\":{" +
                                "\"id\":" + positionResponse.getDepartment().getId() + "," +
                                "\"name\":\"" + positionResponse.getDepartment().getName() + "\"," +
                                "\"positions\":[]" +
                                "}" +
                                "}")
                        .collect(Collectors.joining(","))
                )
                .orElse("");
        String contacts = Optional.ofNullable(response.getContacts())
                .map(contactResponses -> contactResponses.stream()
                        .map(contactResponse -> "{" +
                                "\"id\": " + contactResponse.getId() + "," +
                                "\"type\": \"" + contactResponse.getType() + "\"," +
                                "\"value\": \"" + contactResponse.getValue() + "\"" +
                                "}")
                        .collect(Collectors.joining(","))
                )
                .orElse("");

        return "{" +
                "\"id\":" + response.getId() + "," +
                "\"email\":\"" + response.getEmail() + "\"," +
                "\"firstName\":\"" + response.getFirstName() + "\"," +
                "\"lastName\":\"" + response.getLastName() + "\"," +
                "\"birthDate\":\"" + response.getBirthDate() + "\"," +
                "\"hiringDate\":\"" + response.getHiringDate() + "\"," +
                "\"positions\":[" + positions + "]," +
                "\"contacts\":[" + contacts + "]" +
                "}";
    }
}