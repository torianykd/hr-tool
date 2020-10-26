package ua.com.alevel.nix.hrtool.model.employee.request;

import org.hibernate.validator.constraints.Length;
import ua.com.alevel.nix.hrtool.model.employee.constraint.ContactTypeConstraint;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class UpdateContactRequest {

    @NotNull(message = "Type must not be null")
    @ContactTypeConstraint
    private String type;

    @NotNull(message = "Value must not be null")
    @Length(min = 2, message = "Value must be 2 characters at least")
    private String value;

    public UpdateContactRequest() {
    }

    public UpdateContactRequest(String type, String value) {
        this.type = type.toUpperCase();
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.toUpperCase();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateContactRequest)) return false;
        UpdateContactRequest that = (UpdateContactRequest) o;
        return type.equals(that.type) &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
