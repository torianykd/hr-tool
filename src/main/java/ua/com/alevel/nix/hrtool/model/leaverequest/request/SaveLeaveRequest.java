package ua.com.alevel.nix.hrtool.model.leaverequest.request;

import org.hibernate.validator.constraints.Range;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestType;
import ua.com.alevel.nix.hrtool.model.leaverequest.constraint.EndValueConstraint;
import ua.com.alevel.nix.hrtool.model.leaverequest.constraint.LeaveRequestTypeConstraint;
import ua.com.alevel.nix.hrtool.model.leaverequest.constraint.StartValueConstraint;

import javax.validation.constraints.NotNull;

@EndValueConstraint
public class SaveLeaveRequest {

    @NotNull(message = "Type must not be null")
    @LeaveRequestTypeConstraint
    private String type;

    @NotNull(message = "Start must not be null")
    @Range(min = 1, max = 9999999999L, message = "Birth date must be a valid timestamp")
    @StartValueConstraint
    private long start;

    @NotNull(message = "Start must not be null")
    @Range(min = 1, max = 9999999999L, message = "Birth date must be a valid timestamp")
    private long end;

    private String comment;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.toUpperCase();
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
}
