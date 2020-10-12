package ua.com.alevel.nix.hrtool.service.department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.alevel.nix.hrtool.model.department.Department;
import ua.com.alevel.nix.hrtool.model.department.request.SaveDepartmentRequest;
import ua.com.alevel.nix.hrtool.model.department.response.DepartmentResponse;

public interface DepartmentService {

    Page<Department> findAll(Pageable pageable);

    DepartmentResponse create(SaveDepartmentRequest request);

    void update(long id, SaveDepartmentRequest request);

    void deleteById(long id);
}
