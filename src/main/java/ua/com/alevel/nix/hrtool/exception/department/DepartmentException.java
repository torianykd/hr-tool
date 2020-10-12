package ua.com.alevel.nix.hrtool.exception.department;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class DepartmentException {

    public static ResponseStatusException departmentNotFound(long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Department with id " + id + " not found");
    }

    public static ResponseStatusException duplicateName(String name) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name " + name + " already taken");
    }

}
