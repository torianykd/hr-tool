package ua.com.alevel.nix.hrtool.service.employee;

import org.springframework.stereotype.Service;
import ua.com.alevel.nix.hrtool.exception.employee.ContactException;
import ua.com.alevel.nix.hrtool.exception.employee.EmployeeException;
import ua.com.alevel.nix.hrtool.model.employee.Contact;
import ua.com.alevel.nix.hrtool.model.employee.ContactType;
import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.request.UpdateContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.response.ContactResponse;
import ua.com.alevel.nix.hrtool.repository.ContactRepository;
import ua.com.alevel.nix.hrtool.repository.EmployeeRepository;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final EmployeeRepository employeeRepository;

    public ContactServiceImpl(ContactRepository contactRepository, EmployeeRepository employeeRepository) {
        this.contactRepository = contactRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public ContactResponse create(SaveContactRequest request) {
        Employee employee = getEmployee(request.getEmployeeId());
        Contact contact = new Contact(request);
        contact.setEmployee(employee);
        return ContactResponse.fromContact(
                contactRepository.save(contact)
        );
    }

    @Override
    public void update(long contactId, UpdateContactRequest request) {
        Contact contact = getContact(contactId);
        contact.setType(ContactType.valueOf(request.getType()));
        contact.setValue(request.getValue());
        contactRepository.save(contact);
    }

    @Override
    public void delete(long contactId) {
        contactRepository.delete(
                getContact(contactId)
        );
    }

    private Employee getEmployee(long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> EmployeeException.employeeNotFound(employeeId));
    }

    private Contact getContact(long contactId) {
        return contactRepository.findById(contactId)
                .orElseThrow(() -> ContactException.contactNotFound(contactId));
    }
}
