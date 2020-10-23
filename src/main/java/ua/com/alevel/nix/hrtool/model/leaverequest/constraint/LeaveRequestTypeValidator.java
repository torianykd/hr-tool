package ua.com.alevel.nix.hrtool.model.leaverequest.constraint;

import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LeaveRequestTypeValidator implements ConstraintValidator<LeaveRequestTypeConstraint, String> {
    @Override
    public boolean isValid(String type, ConstraintValidatorContext context) {
        if (type == null) {
            return true;
        }
        try {
            LeaveRequestType.valueOf(type);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
