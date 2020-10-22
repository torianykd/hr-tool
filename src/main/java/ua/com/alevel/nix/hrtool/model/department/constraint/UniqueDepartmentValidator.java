package ua.com.alevel.nix.hrtool.model.department.constraint;

import ua.com.alevel.nix.hrtool.repository.DepartmentRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueDepartmentValidator implements ConstraintValidator<UniqueDepartmentConstraint, String> {

    private final DepartmentRepository departmentRepository;

    public UniqueDepartmentValidator(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public boolean isValid(String departmentName, ConstraintValidatorContext context) {
        if (departmentName == null) {
            return true;
        }

        return !departmentRepository.existsByName(departmentName);
    }
}
