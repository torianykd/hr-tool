package ua.com.alevel.nix.hrtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.alevel.nix.hrtool.model.employee.Employee;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    Optional<Employee> findByEmail(String email);

}
