package ua.com.alevel.nix.hrtool.model.employee.constraint;

import ua.com.alevel.nix.hrtool.model.employee.ContactType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContactTypeValidator implements ConstraintValidator<ContactTypeConstraint, String> {
    @Override
    public boolean isValid(String type, ConstraintValidatorContext context) {
        if (type == null) {
            return true;
        }
        try {
            ContactType.valueOf(type);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
