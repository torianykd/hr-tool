package ua.com.alevel.nix.hrtool.model.employee.request;

import org.hibernate.validator.constraints.Length;
import ua.com.alevel.nix.hrtool.model.employee.constraint.ContactTypeConstraint;

import javax.validation.constraints.NotNull;

public class UpdateContactRequest {

    @NotNull(message = "Type must not be null")
    @ContactTypeConstraint
    private String type;

    @NotNull(message = "Value must not be null")
    @Length(min = 2, message = "Value must be 2 characters at least")
    private String value;

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

}
