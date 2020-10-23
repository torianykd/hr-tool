package ua.com.alevel.nix.hrtool.service.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.alevel.nix.hrtool.exception.employee.EmployeeException;
import ua.com.alevel.nix.hrtool.model.employee.Contact;
import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.employee.EmployeeName;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveEmployeeRequest;
import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;
import ua.com.alevel.nix.hrtool.model.position.Position;
import ua.com.alevel.nix.hrtool.repository.ContactRepository;
import ua.com.alevel.nix.hrtool.repository.EmployeeRepository;
import ua.com.alevel.nix.hrtool.repository.PositionRepository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    EmployeeRepository employeeRepository;
    PositionRepository positionRepository;
    ContactRepository contactRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, PositionRepository positionRepository, ContactRepository contactRepository) {
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public Page<EmployeeResponse> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(EmployeeResponse::fromEmployee);
    }

    @Override
    @Transactional
    public EmployeeResponse create(SaveEmployeeRequest request) {
        validateUnique(request);
        List<Position> positions = validateAndGetPositions(request);

        Employee employee = new Employee(request);
        employee.setPositions(new HashSet<>(positions));
        Employee savedEmployee = employeeRepository.save(employee);

        if (!request.getContacts().isEmpty()) {
            Set<Contact> contacts = request.getContacts().stream()
                    .map(Contact::new)
                    .peek(contact -> contact.setEmployee(savedEmployee))
                    .collect(Collectors.toSet());
            contactRepository.saveAll(contacts);
            savedEmployee.setContacts(contacts);
        }

        return EmployeeResponse.fromEmployee(savedEmployee);
    }

    @Override
    public EmployeeResponse getById(long id) {
        return EmployeeResponse.fromEmployee(getEmployee(id));
    }

    @Override
    public void update(long id, SaveEmployeeRequest request) {
        Employee employee = getEmployee(id);
        if (!request.getEmail().equals(employee.getEmail())) {
            validateUnique(request);
        }
        List<Position> positions = validateAndGetPositions(request);

        employee.setEmail(request.getEmail());
        employee.setEmployeeName(new EmployeeName(request.getFirstName(), request.getLastName()));
        employee.setBirthDate(Instant.ofEpochSecond(request.getBirthDate()));
        employee.setHiringDate(Instant.ofEpochSecond(request.getHiringDate()));
        employee.setPositions(new HashSet<>(positions));
        updateContacts(employee, request.getContacts());
        employeeRepository.save(employee);
    }

    @Override
    public void deleteById(long id) {
        getEmployee(id);
        employeeRepository.deleteById(id);
    }

    private void updateContacts(Employee employee, List<SaveContactRequest> contacts) {
        if (contacts.isEmpty() && !employee.getContacts().isEmpty()) {
            contactRepository.deleteAll(employee.getContacts());
            employee.setContacts(new HashSet<>());
            return;
        }
        Set<Contact> requestContacts = contacts.stream()
                .map(Contact::new)
                .peek(contact -> contact.setEmployee(employee))
                .collect(Collectors.toSet());
        // Save new contacts
        requestContacts.stream()
                .filter(requestContact -> !employee.getContacts().contains(requestContact))
                .forEach(contactRepository::save);
        // Remove contact not present in request
        employee.getContacts().stream()
                .filter(employeeContact -> !requestContacts.contains(employeeContact))
                .peek(employeeContact -> employee.getContacts().remove(employeeContact))
                .forEach(contactRepository::delete);
    }

    private Employee getEmployee(long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> EmployeeException.employeeNotFound(id));
    }

    private List<Position> validateAndGetPositions(SaveEmployeeRequest request) {
        List<Long> positionIds = request.getPositionIds();
        List<Position> foundPositions = positionRepository.findAllById(positionIds);
        if (positionIds.size() != foundPositions.size()) {
            throw EmployeeException.positionsNotExist();
        }
        return foundPositions;
    }

    private void validateUnique(SaveEmployeeRequest request) {
        String email = request.getEmail();
        if (employeeRepository.existsByEmail(email)) {
            throw EmployeeException.duplicateEmail(email);
        }
    }

}
