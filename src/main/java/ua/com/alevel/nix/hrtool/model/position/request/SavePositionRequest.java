package ua.com.alevel.nix.hrtool.model.position.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class SavePositionRequest {

    @Min(value = 0L, message = "Position id must be positive integer")
    private Long departmentId;

    @NotNull(message = "Name must not be null")
    @Size(min = 2, message = "Name must be min 2 symbols")
    private String name;

    public SavePositionRequest() {
    }

    public SavePositionRequest(Long departmentId, String name) {
        this.departmentId = departmentId;
        this.name = name;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavePositionRequest)) return false;
        SavePositionRequest request = (SavePositionRequest) o;
        return Objects.equals(departmentId, request.departmentId) &&
                name.equals(request.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departmentId, name);
    }
}
