package ua.com.alevel.nix.hrtool.model.leaverequest.response;

import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestStatus;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestType;

public class LeaveRequestResponse {

    private long id;

    private LeaveRequestType type;

    private LeaveRequestStatus status;

    private long start;

    private long end;

    private String comment;

    private EmployeeResponse employee;

    public LeaveRequestResponse(long id, LeaveRequestType type, LeaveRequestStatus status, long start, long end, String comment) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.start = start;
        this.end = end;
        this.comment = comment;
    }

    public static LeaveRequestResponse fromLeaveRequestWithBasicAttributes(LeaveRequest leaveRequest) {
        return new LeaveRequestResponse(
                leaveRequest.getId(),
                leaveRequest.getType(),
                leaveRequest.getStatus(),
                leaveRequest.getStart().getEpochSecond(),
                leaveRequest.getEnd().getEpochSecond(),
                leaveRequest.getComment()
        );
    }

    public static LeaveRequestResponse fromLeaveRequest(LeaveRequest leaveRequest) {
        LeaveRequestResponse response = fromLeaveRequestWithBasicAttributes(leaveRequest);
        response.employee = EmployeeResponse.fromEmployeeWithBasicAttributes(leaveRequest.getEmployee());
        return response;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LeaveRequestType getType() {
        return type;
    }

    public void setType(LeaveRequestType type) {
        this.type = type;
    }

    public LeaveRequestStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveRequestStatus status) {
        this.status = status;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public EmployeeResponse getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeResponse employee) {
        this.employee = employee;
    }
}
