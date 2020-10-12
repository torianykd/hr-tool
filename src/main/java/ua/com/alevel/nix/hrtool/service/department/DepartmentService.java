package ua.com.alevel.nix.hrtool.service.department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.alevel.nix.hrtool.model.department.Department;

public interface DepartmentService {

    Page<Department> findAll(Pageable pageable);

}
