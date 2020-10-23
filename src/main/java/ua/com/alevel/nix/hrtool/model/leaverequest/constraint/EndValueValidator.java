package ua.com.alevel.nix.hrtool.model.leaverequest.constraint;

import ua.com.alevel.nix.hrtool.model.leaverequest.request.SaveLeaveRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndValueValidator implements ConstraintValidator<EndValueConstraint, SaveLeaveRequest> {
    @Override
    public boolean isValid(SaveLeaveRequest request, ConstraintValidatorContext context) {
        return request.getEnd().compareTo(request.getStart()) >= 0;
    }
}
