package ua.com.alevel.nix.hrtool.controller.department;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.nix.hrtool.Routes;
import ua.com.alevel.nix.hrtool.model.department.request.SaveDepartmentRequest;
import ua.com.alevel.nix.hrtool.model.department.response.DepartmentResponse;
import ua.com.alevel.nix.hrtool.service.department.DepartmentService;

import javax.validation.Valid;

@RestController
@RequestMapping(Routes.DEPARTMENTS)
@Tag(name = "Departments Resource")
public class DepartmentController {

    DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @PageableAsQueryParam
    public Page<DepartmentResponse> listDepartments(@Parameter(hidden = true) Pageable pageable) {
        return departmentService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public DepartmentResponse get(@PathVariable long id) {
        return departmentService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@RequestBody @Valid SaveDepartmentRequest request) {
        return departmentService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long id, @RequestBody @Valid SaveDepartmentRequest request) {
        departmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        departmentService.deleteById(id);
    }

}
