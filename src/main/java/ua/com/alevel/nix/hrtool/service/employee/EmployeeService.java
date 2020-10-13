package ua.com.alevel.nix.hrtool.service.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveEmployeeRequest;
import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;

public interface EmployeeService {

    Page<EmployeeResponse> findAll(Pageable pageable);

    EmployeeResponse create(SaveEmployeeRequest request);

    EmployeeResponse getById(long id);

    void update(long id, SaveEmployeeRequest request);

    void deleteById(long id);

}
