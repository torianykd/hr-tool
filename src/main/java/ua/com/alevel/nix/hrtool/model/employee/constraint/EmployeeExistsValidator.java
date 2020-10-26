package ua.com.alevel.nix.hrtool.model.employee.constraint;

import ua.com.alevel.nix.hrtool.repository.EmployeeRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmployeeExistsValidator implements ConstraintValidator<EmployeeExistsConstraint, Long> {

    private final EmployeeRepository employeeRepository;

    public EmployeeExistsValidator(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public boolean isValid(Long employeeId, ConstraintValidatorContext context) {
        return employeeRepository.findById(employeeId).isPresent();
    }

}
