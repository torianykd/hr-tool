package ua.com.alevel.nix.hrtool.exception.position;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PositionException {

    public static ResponseStatusException positionNotFound(long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Position with id " + id + " not found");
    }

    public static ResponseStatusException duplicateName(String name) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name " + name + " already taken");
    }

}
