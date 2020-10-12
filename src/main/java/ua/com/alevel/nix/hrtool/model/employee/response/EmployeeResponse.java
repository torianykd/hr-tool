package ua.com.alevel.nix.hrtool.model.employee.response;

import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.position.Position;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public class EmployeeResponse {

    private long id;

    private String email;

    private String firstName;

    private String lastName;

    private long birthDate;

    private long hiringDate;

    private Set<PositionResponse> positions;

    private Set<ContactResponse> contacts;

    public static EmployeeResponse fromEmployee(Employee employee) {
        EmployeeResponse employeeResponse = fromEmployeeWithBasicAttributes(employee);
        employeeResponse.positions = employee.getPositions().stream()
                .map(PositionResponse::fromPositionWithBasicAttributes)
                .collect(Collectors.toSet());
        employeeResponse.contacts = employee.getContacts().stream()
                .map(ContactResponse::fromContactWithBasicAttributes)
                .collect(Collectors.toSet());
        return employeeResponse;
    }

    public static EmployeeResponse fromEmployeeWithBasicAttributes(Employee employee) {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.id = employee.getId();
        employeeResponse.email = employee.getEmail();
        employeeResponse.firstName = employee.getEmployeeName().getFirstName();
        employeeResponse.lastName = employee.getEmployeeName().getLastName();
        employeeResponse.birthDate = employee.getBirthDate().getEpochSecond();
        employeeResponse.hiringDate = employee.getHiringDate().getEpochSecond();
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

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }

    public long getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(long hiringDate) {
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
