package ua.com.alevel.nix.hrtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.alevel.nix.hrtool.model.employee.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
