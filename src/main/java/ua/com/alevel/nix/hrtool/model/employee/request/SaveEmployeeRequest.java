package ua.com.alevel.nix.hrtool.model.employee.request;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class SaveEmployeeRequest {

    @Email
    @NotNull(message = "Email must not be null")
    private String email;

    @NotNull(message = "First name must not be null")
    private String firstName;

    @NotNull(message = "Last name must not be null")
    private String lastName;

    @NotNull(message = "Birth date must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @NotNull(message = "Hiring date must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hiringDate;

    @NotNull
    private List<Long> positionIds;

    public SaveEmployeeRequest() {
    }

    public SaveEmployeeRequest(String email, String firstName, String lastName, LocalDate birthDate, LocalDate hiringDate) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.hiringDate = hiringDate;
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

    public List<Long> getPositionIds() {
        return positionIds;
    }

    public void setPositionIds(List<Long> positionIds) {
        this.positionIds = positionIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SaveEmployeeRequest)) return false;
        SaveEmployeeRequest request = (SaveEmployeeRequest) o;
        return email.equals(request.email) &&
                firstName.equals(request.firstName) &&
                lastName.equals(request.lastName) &&
                birthDate.equals(request.birthDate) &&
                hiringDate.equals(request.hiringDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, firstName, lastName, birthDate, hiringDate);
    }
}
