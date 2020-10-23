package ua.com.alevel.nix.hrtool.model.leaverequest.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {StartValueValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StartValueConstraint {

    String message() default "Start date must be greater or equals then today.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
