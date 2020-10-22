package ua.com.alevel.nix.hrtool.model.employee.request;

import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SaveEmployeeRequest {

    @Email
    @NotNull(message = "Email must not be null")
    private String email;

    @NotNull(message = "First name must not be null")
    private String firstName;

    @NotNull(message = "Last name must not be null")
    private String lastName;

    @NotNull(message = "Birth date must not be null")
    @Range(min = 1, max = 9999999999L, message = "Birth date must be a valid timestamp")
    private Long birthDate = 1L;

    @NotNull(message = "Hiring date must not be null")
    @Range(min = 1, max = 9999999999L, message = "Hiring date must be a valid timestamp")
    private Long hiringDate = 1L;

    private List<Long> positionIds;

    @Valid
    private List<SaveContactRequest> contacts;

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

    public Long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Long birthDate) {
        this.birthDate = birthDate;
    }

    public Long getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(Long hiringDate) {
        this.hiringDate = hiringDate;
    }

    public List<Long> getPositionIds() {
        return positionIds;
    }

    public void setPositionIds(List<Long> positionIds) {
        this.positionIds = positionIds;
    }

    public List<SaveContactRequest> getContacts() {
        return contacts;
    }

    public void setContacts(List<SaveContactRequest> contacts) {
        this.contacts = contacts;
    }
}
