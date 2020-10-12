package ua.com.alevel.nix.hrtool.model.employee;

import ua.com.alevel.nix.hrtool.model.position.Position;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Embedded
    private EmployeeName employeeName;

    @Column(nullable = false)
    private Instant birthDate;

    @Column(nullable = false)
    private Instant hiringDate;

    @ManyToMany
    @JoinTable(
            name = "employee_position",
            joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "position_id", referencedColumnName = "id")
    )
    private Set<Position> positions;

    @OneToMany(mappedBy = "employee")
    private Set<Contact> contacts;

    public Employee() {
    }

    public Employee(Long id, String email, EmployeeName employeeName, Instant birthDate, Instant hiringDate) {
        this.id = id;
        this.email = email;
        this.employeeName = employeeName;
        this.birthDate = birthDate;
        this.hiringDate = hiringDate;
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

    public Instant getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Instant birthDate) {
        this.birthDate = birthDate;
    }

    public Instant getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(Instant hiringDate) {
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
}
