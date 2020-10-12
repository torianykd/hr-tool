package ua.com.alevel.nix.hrtool.controller.department;

import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.alevel.nix.hrtool.model.department.response.DepartmentResponse;
import ua.com.alevel.nix.hrtool.service.department.DepartmentService;

@RestController
@RequestMapping("departments")
public class DepartmentController {

    DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @PageableAsQueryParam
    public Page<DepartmentResponse> listDepartments(@Parameter(hidden = true) Pageable pageable) {
        return departmentService.findAll(pageable).map(DepartmentResponse::fromDepartment);
    }

}
