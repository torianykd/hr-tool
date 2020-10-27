package ua.com.alevel.nix.hrtool.model.leaverequest.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestStatus;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestType;

import java.time.LocalDate;
import java.util.Objects;

public class LeaveRequestResponse {

    private long id;

    private LeaveRequestType type;

    private LeaveRequestStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate start;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate end;

    private String comment;

    private EmployeeResponse employee;

    public LeaveRequestResponse() {
    }

    public LeaveRequestResponse(long id, LeaveRequestType type, LeaveRequestStatus status, LocalDate start, LocalDate end, String comment) {
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
                leaveRequest.getStart(),
                leaveRequest.getEnd(),
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

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaveRequestResponse)) return false;
        LeaveRequestResponse response = (LeaveRequestResponse) o;
        return id == response.id &&
                type == response.type &&
                status == response.status &&
                start.equals(response.start) &&
                end.equals(response.end) &&
                Objects.equals(comment, response.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, status, start, end, comment);
    }
}
