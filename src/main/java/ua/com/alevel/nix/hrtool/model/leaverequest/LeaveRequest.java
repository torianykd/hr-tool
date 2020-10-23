package ua.com.alevel.nix.hrtool.model.leaverequest;

import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.leaverequest.request.SaveLeaveRequest;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveRequestType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveRequestStatus status;

    @Column(nullable = false)
    private Instant start;

    @Column(nullable = false)
    private Instant end;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column
    private String comment;

    public LeaveRequest() {
    }

    public LeaveRequest(SaveLeaveRequest request) {
        this(
                LeaveRequestType.valueOf(request.getType()),
                LeaveRequestStatus.PENDING,
                Instant.ofEpochSecond(request.getStart()),
                Instant.ofEpochSecond(request.getEnd()),
                request.getComment()
        );
    }

    public LeaveRequest(LeaveRequestType type, LeaveRequestStatus status, Instant start, Instant end, String comment) {
        this.type = type;
        this.status = status;
        this.start = start;
        this.end = end;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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
        if (!(o instanceof LeaveRequest)) return false;
        LeaveRequest that = (LeaveRequest) o;
        return type == that.type &&
                status == that.status &&
                start.equals(that.start) &&
                end.equals(that.end) &&
                employee.equals(that.employee) &&
                Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, status, start, end, employee, comment);
    }
}
