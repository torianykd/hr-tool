package ua.com.alevel.nix.hrtool.controller.leaverequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.nix.hrtool.Routes;
import ua.com.alevel.nix.hrtool.model.leaverequest.request.SaveLeaveRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.response.LeaveRequestResponse;
import ua.com.alevel.nix.hrtool.service.leaverequest.LeaveRequestService;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(Routes.LEAVE_REQUESTS)
@Tag(name = "Leave Request Resource")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequestResponse create(@Valid @RequestBody SaveLeaveRequest request,
                                       @AuthenticationPrincipal Principal principal) {
        return leaveRequestService.create(request, principal.getName());
    }

}
