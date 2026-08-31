package org.yvl.teamforge.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.yvl.teamforge.validation.UserSkillRequestValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserSkillRequestValidator.class)
public @interface ValidUserSkillRequest {

    String message() default "Either skillId or skillName and category must be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}