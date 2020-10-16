package ua.com.alevel.nix.hrtool.controller.employee;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.nix.hrtool.Routes;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveEmployeeRequest;
import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;
import ua.com.alevel.nix.hrtool.service.employee.EmployeeService;

import javax.validation.Valid;

@RestController
@RequestMapping(Routes.EMPLOYEES)
@Tag(name = "Employees Resource")
public class EmployeeController {

    EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PageableAsQueryParam
    public Page<EmployeeResponse> listEmployees(@Parameter(hidden = true) Pageable pageable) {
        return employeeService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable long id) {
        return employeeService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@Valid @RequestBody SaveEmployeeRequest request) {
        return employeeService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long id, @Valid @RequestBody SaveEmployeeRequest request) {
        employeeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        employeeService.deleteById(id);
    }

}
