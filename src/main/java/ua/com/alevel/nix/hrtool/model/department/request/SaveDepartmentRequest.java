package ua.com.alevel.nix.hrtool.model.department.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SaveDepartmentRequest {

    @NotNull(message = "Name must not be null")
    @Size(min = 2, message = "Name must be min 2 symbols")
    private String name;

    public SaveDepartmentRequest() {
    }

    public SaveDepartmentRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
