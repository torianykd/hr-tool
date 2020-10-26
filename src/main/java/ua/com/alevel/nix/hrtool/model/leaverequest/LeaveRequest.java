package ua.com.alevel.nix.hrtool.model.leaverequest;

import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.leaverequest.request.SaveLeaveRequest;

import javax.persistence.*;
import java.time.LocalDate;
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
    private LocalDate start;

    @Column(nullable = false)
    private LocalDate end;

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
                request.getStart(),
                request.getEnd(),
                request.getComment()
        );
    }

    public LeaveRequest(LeaveRequestType type, LeaveRequestStatus status, LocalDate start, LocalDate end, String comment) {
        this.type = type;
        this.status = status;
        this.start = start;
        this.end = end;
        this.comment = comment;
    }

    public LeaveRequest(Long id, LeaveRequestType type, LeaveRequestStatus status, LocalDate start, LocalDate end, String comment) {
        this(type, status, start, end, comment);
        this.id = id;
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
