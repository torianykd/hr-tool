package ua.com.alevel.nix.hrtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequest;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

}
