package ua.com.alevel.nix.hrtool.controller.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import ua.com.alevel.nix.hrtool.Routes;
import ua.com.alevel.nix.hrtool.exception.employee.ContactException;
import ua.com.alevel.nix.hrtool.exception.employee.EmployeeException;
import ua.com.alevel.nix.hrtool.model.employee.ContactType;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveEmployeeRequest;
import ua.com.alevel.nix.hrtool.model.employee.request.UpdateContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.response.ContactResponse;
import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;
import ua.com.alevel.nix.hrtool.service.employee.ContactService;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContactControllerTest {

    private MockMvc mvc;

    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactService = mock(ContactService.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new ContactController(contactService))
                .build();
    }

    @Test
    void testCreateContact() throws Exception {
        SaveContactRequest contactRequest = new SaveContactRequest(1L, "phone", "+380");
        ContactResponse contactResponse = new ContactResponse(1L, ContactType.PHONE, "+380");
        contactResponse.setEmployee(new EmployeeResponse(
                1L, "employee@mail.com", "first", "last", LocalDate.now(), LocalDate.now()));

        when(contactService.create(contactRequest))
                .thenReturn(contactResponse);

        mvc.perform(post(Routes.CONTACTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(contactRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(buildJsonStringFromResponse(contactResponse)));

        verify(contactService).create(contactRequest);
        verifyNoMoreInteractions(contactService);
    }

    @Test
    void testInvalidEmployeeOnContactCreate() throws Exception {
        SaveContactRequest contactRequest = new SaveContactRequest(1, "phone", "+380");

        when(contactService.create(contactRequest))
                .thenThrow(EmployeeException.employeeNotFound(1));

        mvc.perform(post(Routes.CONTACTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(contactRequest)))
                .andExpect(status().isNotFound());

        verify(contactService).create(contactRequest);
        verifyNoMoreInteractions(contactService);
    }

    @Test
    void testInvalidContactTypeOnContactCreate() throws Exception {
        SaveContactRequest contactRequest = new SaveContactRequest(1, "random", "+380");

        when(contactService.create(contactRequest))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        mvc.perform(post(Routes.CONTACTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(contactRequest)))
                .andExpect(status().isBadRequest());
        verifyNoMoreInteractions(contactService);
    }

    @Test
    void testUpdateContact() throws Exception {
        long presentId = 1;
        UpdateContactRequest contactRequest = new UpdateContactRequest("phone", "+380");

        doNothing()
                .when(contactService)
                .update(presentId, contactRequest);

        mvc.perform(put(Routes.CONTACTS + "/" + presentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(contactRequest)))
                .andExpect(status().isNoContent());
        verify(contactService).update(presentId, contactRequest);
        verifyNoMoreInteractions(contactService);
    }

    @Test
    void testInvalidContactOnUpdate() throws Exception {
        long absentId = 10;
        UpdateContactRequest contactRequest = new UpdateContactRequest("phone", "+380");

        doThrow(ContactException.contactNotFound(absentId))
                .when(contactService)
                .update(absentId, contactRequest);

        mvc.perform(put(Routes.CONTACTS + "/" + absentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(contactRequest)))
                .andExpect(status().isNotFound());
        verify(contactService).update(absentId, contactRequest);
        verifyNoMoreInteractions(contactService);
    }

    @Test
    void testInvalidContactTypeOnContactUpdate() throws Exception {
        long presentId = 1;
        UpdateContactRequest contactRequest = new UpdateContactRequest("random", "+380");

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(contactService)
                .update(presentId, contactRequest);

        mvc.perform(put(Routes.CONTACTS + "/" + presentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildJsonStringFromRequest(contactRequest)))
                .andExpect(status().isBadRequest());
        verifyNoMoreInteractions(contactService);
    }

    @Test
    void testDeleteContact() throws Exception {
        long presentId = 1;

        doNothing()
                .when(contactService)
                .delete(presentId);

        mvc.perform(delete(Routes.CONTACTS + "/" + presentId))
                .andExpect(status().isNoContent());

        verify(contactService).delete(presentId);
        verifyNoMoreInteractions(contactService);
    }

    @Test
    void testInvalidContactOnDelete() throws Exception {
        long absentId = 10;

        doThrow(ContactException.contactNotFound(absentId))
                .when(contactService)
                .delete(absentId);

        mvc.perform(delete(Routes.CONTACTS + "/" + absentId))
                .andExpect(status().isNotFound());

        verify(contactService).delete(absentId);
        verifyNoMoreInteractions(contactService);
    }

    private String buildJsonStringFromRequest(SaveContactRequest request) {
        return "{" +
                    "\"type\":\"" + request.getType() + "\"," +
                    "\"value\":\"" + request.getValue() + "\"," +
                    "\"employeeId\":" + request.getEmployeeId() +
                "}";
    }

    private String buildJsonStringFromRequest(UpdateContactRequest request) {
        return "{" +
                "\"type\":\"" + request.getType() + "\"," +
                "\"value\":\"" + request.getValue() + "\"" +
                "}";
    }

    private String buildJsonStringFromResponse(ContactResponse response) {
        String employee = Optional.ofNullable(response.getEmployee())
                .map(employeeResponse -> "{" +
                        "\"id\":" + employeeResponse.getId() + "," +
                        "\"email\":\"" + employeeResponse.getEmail() + "\"," +
                        "\"firstName\":\"" + employeeResponse.getFirstName() + "\"," +
                        "\"lastName\":\"" + employeeResponse.getLastName() + "\"," +
                        "\"birthDate\":\"" + employeeResponse.getBirthDate() + "\"," +
                        "\"hiringDate\":\"" + employeeResponse.getHiringDate() + "\"" +
                        "}")
                .orElse(null);
        return "{" +
                    "\"id\":" + response.getId() + "," +
                    "\"type\":\"" + response.getType() +"\"," +
                    "\"value\":\"" + response.getValue() + "\"," +
                    "\"employee\":" + employee +
                "}";
    }
}