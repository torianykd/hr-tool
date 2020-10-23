package ua.com.alevel.nix.hrtool.model.employee;

import ua.com.alevel.nix.hrtool.model.employee.request.SaveEmployeeRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequest;
import ua.com.alevel.nix.hrtool.model.position.Position;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Embedded
    private EmployeeName employeeName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalDate hiringDate;

    @ManyToMany
    @JoinTable(
            name = "employee_position",
            joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "position_id", referencedColumnName = "id")
    )
    private Set<Position> positions;

    @OneToMany(mappedBy = "employee")
    private Set<Contact> contacts;

    @OneToMany(mappedBy = "employee")
    private Set<LeaveRequest> leaveRequests;

    public Employee() {
    }

    public Employee(String email, EmployeeName employeeName, LocalDate birthDate, LocalDate hiringDate) {
        this.email = email;
        this.employeeName = employeeName;
        this.birthDate = birthDate;
        this.hiringDate = hiringDate;
    }

    public Employee(SaveEmployeeRequest request) {
        this(
                request.getEmail(),
                new EmployeeName(request.getFirstName(), request.getLastName()),
                request.getBirthDate(),
               request.getHiringDate()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmployeeName getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(EmployeeName employeeName) {
        this.employeeName = employeeName;
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

    public Set<Position> getPositions() {
        return positions;
    }

    public void setPositions(Set<Position> positions) {
        this.positions = positions;
    }

    public Set<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }

    public Set<LeaveRequest> getLeaveRequests() {
        return leaveRequests;
    }

    public void setLeaveRequests(Set<LeaveRequest> leaveRequests) {
        this.leaveRequests = leaveRequests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return id.equals(employee.id) &&
                email.equals(employee.email) &&
                employeeName.equals(employee.employeeName) &&
                birthDate.equals(employee.birthDate) &&
                hiringDate.equals(employee.hiringDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, employeeName, birthDate, hiringDate);
    }
}
