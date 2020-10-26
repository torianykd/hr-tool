package ua.com.alevel.nix.hrtool.model.employee.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {EmployeeExistsValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmployeeExistsConstraint {

    String message() default "Employee id is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
