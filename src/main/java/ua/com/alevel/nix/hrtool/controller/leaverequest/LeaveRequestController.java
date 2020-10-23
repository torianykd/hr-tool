package ua.com.alevel.nix.hrtool.controller.leaverequest;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    @PageableAsQueryParam
    public Page<LeaveRequestResponse> listRequests(@Parameter(hidden = true) Pageable pageable,
                                                   @AuthenticationPrincipal Principal principal) {
        return leaveRequestService.findAll(pageable, principal.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequestResponse create(@Valid @RequestBody SaveLeaveRequest request,
                                       @AuthenticationPrincipal Principal principal) {
        return leaveRequestService.create(request, principal.getName());
    }

    @PostMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approve(@PathVariable long id) {
        leaveRequestService.approve(id);
    }

    @PostMapping("/{id}/decline")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void decline(@PathVariable long id) {
        leaveRequestService.decline(id);
    }

}
