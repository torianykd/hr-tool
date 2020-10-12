package ua.com.alevel.nix.hrtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.alevel.nix.hrtool.model.department.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByName(String name);
}
