package ua.com.alevel.nix.hrtool.model.employee.response;

import ua.com.alevel.nix.hrtool.model.employee.Contact;
import ua.com.alevel.nix.hrtool.model.employee.ContactType;

public class ContactResponse {

    private long id;

    private ContactType type;

    private String value;

    private EmployeeResponse employee;

    public ContactResponse(long id, ContactType type, String value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public static ContactResponse fromContactWithBasicAttributes(Contact contact) {
        return new ContactResponse(
                contact.getId(),
                contact.getType(),
                contact.getValue()
        );
    }

    public static ContactResponse fromContact(Contact contact) {
        ContactResponse contactResponse = fromContactWithBasicAttributes(contact);
        contactResponse.setEmployee(
                EmployeeResponse.fromEmployeeWithBasicAttributes(contact.getEmployee())
        );
        return contactResponse;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public EmployeeResponse getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeResponse employee) {
        this.employee = employee;
    }
}
