package org.yvl.teamforge.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.yvl.teamforge.skill.dto.request.UserSkillRequest;
import org.yvl.teamforge.validation.annotation.ValidUserSkillRequest;

public class UserSkillRequestValidator implements ConstraintValidator<ValidUserSkillRequest, UserSkillRequest> {

    @Override
    public boolean isValid(UserSkillRequest request, ConstraintValidatorContext context) {

        if (request.getSkillId() != null) {
            return true;
        }

        return request.getSkillName() != null && !request.getSkillName().isBlank() && request.getCategory() != null;
    }
}
