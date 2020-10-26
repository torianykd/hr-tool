package ua.com.alevel.nix.hrtool.model.employee.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.position.Position;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EmployeeResponse {

    private long id;

    private String email;

    private String firstName;

    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate birthDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate hiringDate;

    private Set<PositionResponse> positions;

    private Set<ContactResponse> contacts;

    public EmployeeResponse(long id, String email, String firstName, String lastName, LocalDate birthDate, LocalDate hiringDate) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.hiringDate = hiringDate;
    }

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
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmail(),
                employee.getEmployeeName().getFirstName(),
                employee.getEmployeeName().getLastName(),
                employee.getBirthDate(),
                employee.getHiringDate()
        );
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeResponse)) return false;
        EmployeeResponse that = (EmployeeResponse) o;
        return id == that.id &&
                email.equals(that.email) &&
                firstName.equals(that.firstName) &&
                lastName.equals(that.lastName) &&
                birthDate.equals(that.birthDate) &&
                hiringDate.equals(that.hiringDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, birthDate, hiringDate);
    }
}
