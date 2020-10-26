package ua.com.alevel.nix.hrtool.model.employee.request;


import ua.com.alevel.nix.hrtool.model.employee.constraint.EmployeeExistsConstraint;

import javax.validation.constraints.NotNull;

public class SaveContactRequest extends UpdateContactRequest {

    @NotNull
    @EmployeeExistsConstraint
    private long employeeId;

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
}
