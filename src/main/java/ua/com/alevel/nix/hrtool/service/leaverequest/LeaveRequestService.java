package ua.com.alevel.nix.hrtool.service.leaverequest;

import ua.com.alevel.nix.hrtool.model.leaverequest.request.SaveLeaveRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.response.LeaveRequestResponse;

public interface LeaveRequestService {

    LeaveRequestResponse create(SaveLeaveRequest request, String employeeEmail);

    void approve(long id);

    void decline(long id);

}
