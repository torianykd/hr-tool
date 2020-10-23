package ua.com.alevel.nix.hrtool.model.leaverequest.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {EndValueValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EndValueConstraint {

    String message() default "End date must be past start date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
