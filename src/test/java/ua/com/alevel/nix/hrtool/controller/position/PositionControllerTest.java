package ua.com.alevel.nix.hrtool.controller.position;

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
import ua.com.alevel.nix.hrtool.exception.department.DepartmentException;
import ua.com.alevel.nix.hrtool.exception.position.PositionException;
import ua.com.alevel.nix.hrtool.model.department.response.DepartmentResponse;
import ua.com.alevel.nix.hrtool.model.position.request.SavePositionRequest;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;
import ua.com.alevel.nix.hrtool.service.position.PositionService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PositionControllerTest {

    private MockMvc mvc;

    private PositionService positionService;

    @BeforeEach
    void setUp() {
        positionService = mock(PositionService.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new PositionController(positionService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void testListPositions() throws Exception {
        PositionResponse response = new PositionResponse(1L, "position");
        response.setDepartment(null);
        Pageable pageable = PageRequest.of(0, 20);
        Page<PositionResponse> pageResponse = new PageImpl<>(List.of(response));

        String expectedJson = buildJsonStringFromResponse(response);

        when(positionService.findAll(pageable))
                .thenReturn(pageResponse);

        mvc.perform(get(Routes.POSITIONS).param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(expectedJson)));

        verify(positionService).findAll(pageable);
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void testGetPosition() throws Exception {
        long presentId = 1;
        DepartmentResponse departmentResponse = new DepartmentResponse(1L, "department");
        departmentResponse.setPositions(Set.of());
        PositionResponse positionResponse = new PositionResponse(presentId, "position");
        positionResponse.setDepartment(departmentResponse);

        when(positionService.getById(presentId))
                .thenReturn(positionResponse);

        String expectedJson = buildJsonStringFromResponse(positionResponse);

        mvc.perform(get(Routes.POSITIONS + "/" + presentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(positionService).getById(presentId);
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void testInvalidPositionIdOnGet() throws Exception {
        long absentId = 10;

        when(positionService.getById(absentId))
                .thenThrow(PositionException.positionNotFound(absentId));

        mvc.perform(get(Routes.POSITIONS + "/" + absentId))
                .andExpect(status().isNotFound());

        verify(positionService).getById(absentId);
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void testCreatePosition() throws Exception {
        SavePositionRequest request = new SavePositionRequest(1L, "position");
        DepartmentResponse departmentResponse = new DepartmentResponse(1L, "department");
        departmentResponse.setPositions(Set.of());
        PositionResponse response = new PositionResponse(1L, "position");
        response.setDepartment(departmentResponse);

        when(positionService.create(request))
                .thenReturn(response);

        String expectedJson = buildJsonStringFromResponse(response);

        mvc.perform(post(Routes.POSITIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(positionService).create(request);
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void testDuplicateNameOnCreatePosition() throws Exception {
        SavePositionRequest duplicateNameRequest = new SavePositionRequest(1L, "duplicate");

        when(positionService.create(duplicateNameRequest))
                .thenThrow(PositionException.duplicateName(duplicateNameRequest.getName()));

        mvc.perform(post(Routes.POSITIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(duplicateNameRequest)))
                .andExpect(status().isBadRequest());

        verify(positionService).create(duplicateNameRequest);
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void testInvalidDepartmentOnCreatePosition() throws Exception {
        SavePositionRequest invalidDepartmentRequest = new SavePositionRequest(1L, "valid");

        when(positionService.create(invalidDepartmentRequest))
                .thenThrow(DepartmentException.departmentNotFound(invalidDepartmentRequest.getDepartmentId()));

        mvc.perform(post(Routes.POSITIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(invalidDepartmentRequest)))
                .andExpect(status().isNotFound());

        verify(positionService).create(invalidDepartmentRequest);
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void testUpdatePosition() throws Exception {
        long presentId = 1;
        SavePositionRequest request = new SavePositionRequest(2L, "position");

        doNothing()
                .when(positionService)
                .update(presentId, request);

        mvc.perform(put(Routes.POSITIONS + "/" + presentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(request)))
                .andExpect(status().isNoContent());

        verify(positionService).update(presentId, request);
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void testInvalidPositionIdOnUpdate() throws Exception {
        long absentId = 10;
        SavePositionRequest request = new SavePositionRequest(2L, "position");

        doThrow(PositionException.positionNotFound(2L))
                .when(positionService)
                .update(absentId, request);

        mvc.perform(put(Routes.POSITIONS + "/" + absentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(request)))
                .andExpect(status().isNotFound());

        verify(positionService).update(absentId, request);
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void testDuplicateNameOnUpdatePosition() throws Exception {
        long presentId = 1;
        SavePositionRequest duplicateNameRequest = new SavePositionRequest(1L, "duplicate");

        doThrow(PositionException.duplicateName(duplicateNameRequest.getName()))
                .when(positionService)
                .update(presentId, duplicateNameRequest);

        mvc.perform(put(Routes.POSITIONS + "/" + presentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(duplicateNameRequest)))
                .andExpect(status().isBadRequest());

        verify(positionService).update(presentId, duplicateNameRequest);
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void testInvalidDepartmentIdOnUpdatePosition() throws Exception {
        long presentId = 1;
        SavePositionRequest invalidDepartmentRequest = new SavePositionRequest(1L, "valid");

        doThrow(DepartmentException.departmentNotFound(invalidDepartmentRequest.getDepartmentId()))
                .when(positionService)
                .update(presentId, invalidDepartmentRequest);

        mvc.perform(put(Routes.POSITIONS + "/" + presentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"departmentId\":1,\"name\":\"valid\"}"))
                .andExpect(status().isNotFound());

        verify(positionService).update(presentId, invalidDepartmentRequest);
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void testDeletePosition() throws Exception {
        long presentId = 1;

        doNothing()
                .when(positionService)
                .delete(presentId);

        mvc.perform(delete(Routes.POSITIONS + "/" + presentId))
                .andExpect(status().isNoContent());

        verify(positionService).delete(presentId);
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void testInvalidPositionIdOnDelete() throws Exception {
        long absentId = 1;

        doThrow(PositionException.positionNotFound(absentId))
                .when(positionService)
                .delete(absentId);

        mvc.perform(delete(Routes.POSITIONS + "/" + absentId))
                .andExpect(status().isNotFound());

        verify(positionService).delete(absentId);
        verifyNoMoreInteractions(positionService);
    }

    private String buildJsonStringFromRequest(SavePositionRequest request) {
        return "{" +
                "\"departmentId\":" + request.getDepartmentId() + "," +
                "\"name\":\"" + request.getName() + "\"" +
                "}";
    }

    private String buildJsonStringFromResponse(PositionResponse response) {
        String department = Optional.ofNullable(response.getDepartment())
                .map(departmentResponse -> "{" +
                        "\"id\":" + departmentResponse.getId() + "," +
                        "\"name\":\"" + departmentResponse.getName() + "\"," +
                        "\"positions\":[]" +
                        "}")
                .orElse(null);
        return "{" +
                "\"id\":" + response.getId() + "," +
                "\"name\":\"" + response.getName() + "\"," +
                "\"department\":" + department +
                "}";
    }

}