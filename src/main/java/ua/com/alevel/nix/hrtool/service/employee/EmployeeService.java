package ua.com.alevel.nix.hrtool.service.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.alevel.nix.hrtool.model.employee.Employee;

public interface EmployeeService {

    Page<Employee> findAll(Pageable pageable);

}
