package ua.com.alevel.nix.hrtool.controller.employee;

import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;
import ua.com.alevel.nix.hrtool.service.employee.EmployeeService;

@RestController
@RequestMapping("employees")
public class EmployeeController {

    EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PageableAsQueryParam
    public Page<EmployeeResponse> listEmployees(@Parameter(hidden = true) Pageable pageable) {
        return employeeService.findAll(pageable).map(EmployeeResponse::fromEmployee);
    }

}
