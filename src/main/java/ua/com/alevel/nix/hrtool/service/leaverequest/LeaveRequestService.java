package ua.com.alevel.nix.hrtool.service.leaverequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.alevel.nix.hrtool.model.leaverequest.request.SaveLeaveRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.response.LeaveRequestResponse;

public interface LeaveRequestService {

    Page<LeaveRequestResponse> findAll(Pageable pageable, String employeeEmail);

    LeaveRequestResponse create(SaveLeaveRequest request, String employeeEmail);

    void update(long id, SaveLeaveRequest request, String employeeEmail);

    void deleteById(long id, String employeeEmail);

    void approve(long id);

    void decline(long id);

}
