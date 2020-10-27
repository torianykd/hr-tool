package ua.com.alevel.nix.hrtool.model.leaverequest.request;

import org.springframework.format.annotation.DateTimeFormat;
import ua.com.alevel.nix.hrtool.model.leaverequest.constraint.EndValueConstraint;
import ua.com.alevel.nix.hrtool.model.leaverequest.constraint.LeaveRequestTypeConstraint;
import ua.com.alevel.nix.hrtool.model.leaverequest.constraint.StartValueConstraint;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

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

    public SaveLeaveRequest() {
    }

    public SaveLeaveRequest(String type, LocalDate start, LocalDate end, String comment) {
        this.type = type.toUpperCase();
        this.start = start;
        this.end = end;
        this.comment = comment;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SaveLeaveRequest)) return false;
        SaveLeaveRequest request = (SaveLeaveRequest) o;
        return type.equals(request.type) &&
                start.equals(request.start) &&
                end.equals(request.end) &&
                Objects.equals(comment, request.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, start, end, comment);
    }
}
