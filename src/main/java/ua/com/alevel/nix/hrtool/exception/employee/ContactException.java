package ua.com.alevel.nix.hrtool.exception.employee;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ContactException {

    public static ResponseStatusException contactNotFound(long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact with id " + id + " not found");
    }

}
