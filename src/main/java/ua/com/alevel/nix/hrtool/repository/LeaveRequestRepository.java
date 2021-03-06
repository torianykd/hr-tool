package ua.com.alevel.nix.hrtool.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestStatus;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @Query("select lr from LeaveRequest lr " +
            "where lr.employee = :employee and lr.status in :status and lr.start <= :end and lr.end >= :start")
    List<LeaveRequest> requestsForPeriod(
            Employee employee, LocalDate start, LocalDate end, Collection<LeaveRequestStatus> status);

    @Query("select lr from LeaveRequest lr " +
            "where lr.employee = :employee and lr.status in :status " +
            "and lr.start <= :end and lr.end >= :start and lr.id <> :id")
    List<LeaveRequest> requestsForPeriod(
            Employee employee, LocalDate start, LocalDate end, Collection<LeaveRequestStatus> status, long id);

    Page<LeaveRequest> findAllByEmployeeOrderByIdDesc(Pageable pageable, Employee employee);

    Page<LeaveRequest> findAllByStatus(Pageable pageable, LeaveRequestStatus status);
}
