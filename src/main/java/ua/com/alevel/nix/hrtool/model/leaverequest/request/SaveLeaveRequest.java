package ua.com.alevel.nix.hrtool.model.leaverequest.request;

import org.springframework.format.annotation.DateTimeFormat;
import ua.com.alevel.nix.hrtool.model.leaverequest.constraint.EndValueConstraint;
import ua.com.alevel.nix.hrtool.model.leaverequest.constraint.LeaveRequestTypeConstraint;
import ua.com.alevel.nix.hrtool.model.leaverequest.constraint.StartValueConstraint;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@EndValueConstraint
public class SaveLeaveRequest {

    @NotNull(message = "Type must not be null")
    @LeaveRequestTypeConstraint
    private String type;

    @NotNull(message = "Start must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @StartValueConstraint
    private LocalDate start;

    @NotNull(message = "Start must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate end;

    private String comment;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.toUpperCase();
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
}
