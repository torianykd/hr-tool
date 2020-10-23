package ua.com.alevel.nix.hrtool.model.employee.response;

import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.position.Position;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EmployeeResponse {

    private long id;

    private String email;

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private LocalDate hiringDate;

    private Set<PositionResponse> positions;

    private Set<ContactResponse> contacts;

    public static EmployeeResponse fromEmployee(Employee employee) {
        EmployeeResponse employeeResponse = fromEmployeeWithBasicAttributes(employee);
        if (Optional.ofNullable(employee.getPositions()).isPresent() && !employee.getPositions().isEmpty()) {
            employeeResponse.positions = employee.getPositions().stream()
                    .map(PositionResponse::fromPositionWithBasicAttributes)
                    .collect(Collectors.toSet());
        }
        if (Optional.ofNullable(employee.getContacts()).isPresent() && !employee.getContacts().isEmpty()) {
            employeeResponse.contacts = employee.getContacts().stream()
                    .map(ContactResponse::fromContactWithBasicAttributes)
                    .collect(Collectors.toSet());
        }
        return employeeResponse;
    }

    public static EmployeeResponse fromEmployeeWithBasicAttributes(Employee employee) {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.id = employee.getId();
        employeeResponse.email = employee.getEmail();
        employeeResponse.firstName = employee.getEmployeeName().getFirstName();
        employeeResponse.lastName = employee.getEmployeeName().getLastName();
        employeeResponse.birthDate = employee.getBirthDate();
        employeeResponse.hiringDate = employee.getHiringDate();
        return employeeResponse;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(LocalDate hiringDate) {
        this.hiringDate = hiringDate;
    }

    public Set<PositionResponse> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionResponse> positions) {
        this.positions = positions;
    }

    public Set<ContactResponse> getContacts() {
        return contacts;
    }

    public void setContacts(Set<ContactResponse> contacts) {
        this.contacts = contacts;
    }
}
