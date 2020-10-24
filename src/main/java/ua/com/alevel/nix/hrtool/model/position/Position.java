package ua.com.alevel.nix.hrtool.model.position;

import ua.com.alevel.nix.hrtool.model.department.Department;
import ua.com.alevel.nix.hrtool.model.employee.Employee;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(mappedBy = "positions")
    private Set<Employee> employees;

    public Position() {
    }

    public Position(String name) {
        this.name = name;
    }

    public Position(Long id, String name) {
        this(name);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return id.equals(position.id) &&
                name.equals(position.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
