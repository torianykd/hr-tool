package ua.com.alevel.nix.hrtool.controller.department;

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
import ua.com.alevel.nix.hrtool.model.department.request.SaveDepartmentRequest;
import ua.com.alevel.nix.hrtool.model.department.response.DepartmentResponse;
import ua.com.alevel.nix.hrtool.service.department.DepartmentService;


import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DepartmentControllerTest {

    private MockMvc mvc;

    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        departmentService = mock(DepartmentService.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new DepartmentController(departmentService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void testListDepartments() throws Exception {
        DepartmentResponse response = new DepartmentResponse(1L, "department");
        response.setPositions(Set.of());
        Pageable pageable = PageRequest.of(0, 20);
        Page<DepartmentResponse> pageResponse = new PageImpl<>(List.of(response));

        String expectedJson = "{\"id\":1,\"name\":\"department\",\"positions\":[]}";

        when(departmentService.findAll(pageable))
                .thenReturn(pageResponse);

        mvc.perform(get(Routes.DEPARTMENTS + "?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(expectedJson)));
        verify(departmentService).findAll(pageable);
    }

    @Test
    void testGetDepartment() throws Exception {
        long presentId = 1;
        DepartmentResponse response = new DepartmentResponse(presentId, "department");
        response.setPositions(Set.of());

        when(departmentService.getById(presentId))
                .thenReturn(response);

        String expectedJson = "{\"id\":" + presentId + ",\"name\":\"department\",\"positions\":[]}";

        mvc.perform(get(Routes.DEPARTMENTS + "/" + presentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(departmentService).getById(presentId);
    }

    @Test
    void testGetUnExistingDepartment() throws Exception {
        long absentId = 10;

        when(departmentService.getById(absentId))
                .thenThrow(DepartmentException.departmentNotFound(absentId));

        mvc.perform(get(Routes.DEPARTMENTS + "/" + absentId))
                .andExpect(status().isNotFound());
        verify(departmentService).getById(absentId);
        verifyNoMoreInteractions(departmentService);
    }

    @Test
    void testCreateDepartment() throws Exception {
        SaveDepartmentRequest request = new SaveDepartmentRequest("department");
        long id = 1;
        DepartmentResponse response = new DepartmentResponse(id, "department");
        response.setPositions(Set.of());

        when(departmentService.create(request)).thenReturn(response);

        String expectedJson = "{\"id\":1,\"name\":\"department\",\"positions\":[]}";

        mvc.perform(post(Routes.DEPARTMENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"department\"}")
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        verify(departmentService).create(request);
    }

    @Test
    void testNoUniqueCreateDepartment() throws Exception {
        SaveDepartmentRequest request = new SaveDepartmentRequest("department");

        when(departmentService.create(request))
                .thenThrow(DepartmentException.duplicateName("department"));

        mvc.perform(post(Routes.DEPARTMENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"department\"}")
        )
                .andExpect(status().isBadRequest());
        verify(departmentService).create(request);
    }

    @Test
    void update() throws Exception {
        SaveDepartmentRequest request = new SaveDepartmentRequest("new department");
        SaveDepartmentRequest duplicate = new SaveDepartmentRequest("duplicate department");
        long presentId = 1;
        long absentId = 10;

        doThrow(DepartmentException.departmentNotFound(absentId))
                .when(departmentService)
                .update(absentId, request);
        doThrow(DepartmentException.duplicateName(duplicate.getName()))
                .when(departmentService)
                .update(presentId, duplicate);
        doNothing()
                .when(departmentService)
                .update(presentId, request);

        mvc.perform(put(Routes.DEPARTMENTS + "/" + absentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"new department\"}"))
                .andExpect(status().isNotFound());
        verify(departmentService).update(absentId, request);

        mvc.perform(put(Routes.DEPARTMENTS + "/" + presentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"duplicate department\"}"))
                .andExpect(status().isBadRequest());
        verify(departmentService).update(presentId, duplicate);

        mvc.perform(put(Routes.DEPARTMENTS + "/" + presentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"new department\"}"))
                .andExpect(status().isNoContent());
        verify(departmentService).update(presentId, request);
        verifyNoMoreInteractions(departmentService);
    }

    @Test
    void testDeleteDepartment() throws Exception {
        long presentId = 1;

        doNothing()
                .when(departmentService)
                .deleteById(presentId);

        mvc.perform(delete(Routes.DEPARTMENTS + "/" + presentId))
                .andExpect(status().isNoContent());
        verify(departmentService).deleteById(presentId);
        verifyNoMoreInteractions(departmentService);
    }

    @Test
    void testDeleteUnExistingDepartment() throws Exception {
        long absentId = 10;

        doThrow(DepartmentException.departmentNotFound(absentId))
                .when(departmentService)
                .deleteById(absentId);

        mvc.perform(delete(Routes.DEPARTMENTS + "/" + absentId))
                .andExpect(status().isNotFound());

        verify(departmentService).deleteById(absentId);
        verifyNoMoreInteractions(departmentService);
    }
}