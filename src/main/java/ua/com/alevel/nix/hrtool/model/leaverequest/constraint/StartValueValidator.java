package ua.com.alevel.nix.hrtool.model.leaverequest.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.*;


public class StartValueValidator implements ConstraintValidator<StartValueConstraint, Long> {
    @Override
    public boolean isValid(Long start, ConstraintValidatorContext context) {
        long today = LocalDate.now().atStartOfDay().toEpochSecond(
                OffsetDateTime.now().getOffset()
        );
        return start >= today;
    }
}
