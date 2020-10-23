package ua.com.alevel.nix.hrtool.exception.leaverequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class LeaveRequestException {

    public static ResponseStatusException requestAlreadyExists() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request for selected period already exists.");
    }

}
