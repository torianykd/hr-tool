package ua.com.alevel.nix.hrtool.model.leaverequest.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {LeaveRequestTypeValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LeaveRequestTypeConstraint {

    String message() default "Leave request type is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
