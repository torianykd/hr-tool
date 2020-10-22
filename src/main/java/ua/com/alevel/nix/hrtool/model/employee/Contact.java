package ua.com.alevel.nix.hrtool.model.employee;

import ua.com.alevel.nix.hrtool.model.employee.request.SaveContactRequest;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContactType type;

    @Column(nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public Contact() {
    }

    public Contact(ContactType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Contact(SaveContactRequest request) {
        type = ContactType.valueOf(request.getType());
        value = request.getValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContactType getType() {
        return type;
    }

    public void setType(ContactType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return type == contact.type &&
                value.equals(contact.value) &&
                employee.equals(contact.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value, employee);
    }
}
