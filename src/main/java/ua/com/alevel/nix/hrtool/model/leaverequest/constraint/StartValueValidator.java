package ua.com.alevel.nix.hrtool.model.leaverequest.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.*;


public class StartValueValidator implements ConstraintValidator<StartValueConstraint, LocalDate> {
    @Override
    public boolean isValid(LocalDate start, ConstraintValidatorContext context) {
        return start.compareTo(LocalDate.now()) >= 0;
    }
}
