package ua.com.alevel.nix.hrtool.model.department.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueDepartmentValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueDepartmentConstraint {

    String message() default "Name already taken";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
