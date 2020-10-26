package ua.com.alevel.nix.hrtool.model.employee.request;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class SaveContactRequest extends UpdateContactRequest {

    @NotNull
    private long employeeId;

    public SaveContactRequest() {
    }

    public SaveContactRequest(long employeeId, String type, String value) {
        super(type, value);
        this.employeeId = employeeId;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SaveContactRequest)) return false;
        if (!super.equals(o)) return false;
        SaveContactRequest that = (SaveContactRequest) o;
        return employeeId == that.employeeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), employeeId);
    }
}
