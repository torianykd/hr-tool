package ua.com.alevel.nix.hrtool.exception.employee;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmployeeException {

    public static ResponseStatusException positionsNotExist() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some position ids were incorrect");
    }

    public static ResponseStatusException duplicateEmail(String email) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email " + email + " already taken");
    }

    public static ResponseStatusException employeeNotFound(long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee with id " + id + " not found");
    }

}
