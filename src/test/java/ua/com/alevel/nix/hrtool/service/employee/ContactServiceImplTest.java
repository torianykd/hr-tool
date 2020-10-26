package ua.com.alevel.nix.hrtool.service.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ua.com.alevel.nix.hrtool.model.employee.Contact;
import ua.com.alevel.nix.hrtool.model.employee.ContactType;
import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.employee.EmployeeName;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.request.UpdateContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.response.ContactResponse;
import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;
import ua.com.alevel.nix.hrtool.repository.ContactRepository;
import ua.com.alevel.nix.hrtool.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactServiceImplTest {

    private EmployeeRepository employeeRepository;

    private ContactRepository contactRepository;

    private ContactServiceImpl contactService;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        contactRepository = mock(ContactRepository.class);
        contactService = new ContactServiceImpl(contactRepository, employeeRepository);
    }

    @Test
    void create() {
        long presentEmployeeId = 1;
        long absentEmployeeId = 10;
        long contactId = 1;
        Employee employee = new Employee(
                presentEmployeeId, "email@,ail.com", new EmployeeName("First", "Last"), LocalDate.now(), LocalDate.now());
        SaveContactRequest invalidEmployeeRequest = new SaveContactRequest(absentEmployeeId, "phone", "+380");
        SaveContactRequest request = new SaveContactRequest(presentEmployeeId, "phone", "+380");

        when(employeeRepository.findById(absentEmployeeId)).thenReturn(Optional.empty());
        when(employeeRepository.findById(presentEmployeeId)).thenReturn(Optional.of(employee));
        when(contactRepository.save(notNull())).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            assertNull(contact.getId());
            assertEquals(request.getType(), contact.getType().toString());
            assertEquals(request.getValue(), contact.getValue());
            assertEquals(request.getEmployeeId(), contact.getEmployee().getId());
            contact.setId(contactId);
            return contact;
        });

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> contactService.create(invalidEmployeeRequest))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(employeeRepository).findById(absentEmployeeId);

        ContactResponse response = contactService.create(request);
        assertEquals(contactId, response.getId());
        assertEquals(request.getType(), response.getType().toString());
        assertEquals(request.getValue(), response.getValue());
        assertEquals(EmployeeResponse.fromEmployeeWithBasicAttributes(employee), response.getEmployee());
        verify(employeeRepository).findById(presentEmployeeId);
        verify(contactRepository).save(notNull());

        verifyNoMoreInteractions(employeeRepository, contactRepository);
    }

    @Test
    void update() {
        long absentId = 10;
        long presentId = 1;
        Contact contact = new Contact(presentId, ContactType.PHONE, "+380");
        UpdateContactRequest request = new UpdateContactRequest(ContactType.PHONE.toString(), "+700");

        when(contactRepository.findById(absentId)).thenReturn(Optional.empty());
        when(contactRepository.findById(presentId)).thenReturn(Optional.of(contact));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> contactService.update(absentId, request))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(contactRepository).findById(absentId);

        contactService.update(presentId, request);
        assertEquals(request.getType(), contact.getType().toString());
        assertEquals(request.getValue(), contact.getValue());

        verify(contactRepository).findById(presentId);
        verify(contactRepository).save(contact);

        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void delete() {
        long absentId = 10;
        long presentId = 1;

        Contact contact = new Contact(presentId, ContactType.PHONE, "+380");

        when(contactRepository.findById(absentId)).thenReturn(Optional.empty());
        when(contactRepository.findById(presentId)).thenReturn(Optional.of(contact));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> contactService.delete(absentId))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(contactRepository).findById(absentId);

        contactService.delete(presentId);
        verify(contactRepository).findById(presentId);
        verify(contactRepository).delete(contact);

        verifyNoMoreInteractions(contactRepository);
    }
}