package ua.com.alevel.nix.hrtool.service.department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.com.alevel.nix.hrtool.exception.department.DepartmentException;
import ua.com.alevel.nix.hrtool.model.department.Department;
import ua.com.alevel.nix.hrtool.model.department.request.SaveDepartmentRequest;
import ua.com.alevel.nix.hrtool.model.department.response.DepartmentResponse;
import ua.com.alevel.nix.hrtool.repository.DepartmentRepository;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Page<DepartmentResponse> findAll(Pageable pageable) {
        return departmentRepository.findAll(pageable).map(DepartmentResponse::fromDepartment);
    }

    @Override
    public DepartmentResponse create(SaveDepartmentRequest request) {
        return DepartmentResponse.fromDepartmentWithBasicAttributes(
                departmentRepository.save(new Department(request.getName()))
        );
    }

    @Override
    public void update(long id, SaveDepartmentRequest request) {
        Department department = getDepartment(id);
        department.setName(request.getName());
        departmentRepository.save(department);
    }

    public void deleteById(long id) {
        getDepartment(id);
        departmentRepository.deleteById(id);
    }

    @Override
    public DepartmentResponse getById(long id) {
        return DepartmentResponse.fromDepartment(getDepartment(id));
    }

    private Department getDepartment(long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> DepartmentException.departmentNotFound(id));
    }

    private void validateUnique(SaveDepartmentRequest request) {
        String name = request.getName();
        if (departmentRepository.existsByName(name)) {
            throw DepartmentException.duplicateName(name);
        }
    }
}
