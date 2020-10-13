package ua.com.alevel.nix.hrtool.model.employee.request;

import ua.com.alevel.nix.hrtool.model.employee.ContactType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SaveContactRequest {

    @NotNull(message = "Type must not be null")
    private ContactType type;

    @NotNull(message = "Value must not be null")
    @Min(value = 2, message = "Value must be 2 characters at least")
    private String value;

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
}
