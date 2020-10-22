package ua.com.alevel.nix.hrtool.model.employee.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {ContactTypeValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContactTypeConstraint {

    String message() default "Contact type is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
