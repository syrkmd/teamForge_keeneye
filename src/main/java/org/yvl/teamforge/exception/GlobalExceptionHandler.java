package org.yvl.teamforge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.yvl.teamforge.admin.exception.AdminTargetModificationNotAllowedException;
import org.yvl.teamforge.auth.exception.EmailAlreadyExistsException;
import org.yvl.teamforge.auth.exception.InvalidAuthenticatedPrincipal;
import org.yvl.teamforge.auth.exception.InvalidCredentialsException;
import org.yvl.teamforge.auth.exception.UserBlockedException;
import org.yvl.teamforge.analytics.exception.TeamAnalyticsNotFoundException;
import org.yvl.teamforge.exception.dto.ApiError;
import org.yvl.teamforge.invitation.exception.InvitationAccessDeniedException;
import org.yvl.teamforge.invitation.exception.InvitationAlreadyExistsException;
import org.yvl.teamforge.invitation.exception.InvitationAlreadyRespondedException;
import org.yvl.teamforge.invitation.exception.InvitationNotFoundException;
import org.yvl.teamforge.matching.exception.ProjectRoleHasNoSkillRequirementsException;
import org.yvl.teamforge.project.exception.InvalidProjectStatusTransitionException;
import org.yvl.teamforge.project.exception.ProjectAccessDeniedException;
import org.yvl.teamforge.project.exception.ProjectArchivedException;
import org.yvl.teamforge.project.exception.ProjectNotFoundException;
import org.yvl.teamforge.project.exception.ProjectRoleNotBelongToProjectException;
import org.yvl.teamforge.project.exception.ProjectRoleNotFoundException;
import org.yvl.teamforge.project.exception.ProjectRoleHasTeamMembersException;
import org.yvl.teamforge.project.exception.ProjectRoleNotOpenException;
import org.yvl.teamforge.project.exception.ProjectRoleSkillAlreadyExistsException;
import org.yvl.teamforge.project.exception.ProjectRoleSkillNotFoundException;
import org.yvl.teamforge.project.exception.ProjectTemplateNotFoundException;
import org.yvl.teamforge.refreshToken.exception.InvalidRefreshTokenException;
import org.yvl.teamforge.skill.exception.SkillAlreadyExistsException;
import org.yvl.teamforge.skill.exception.SkillCategoryNotFoundException;
import org.yvl.teamforge.skill.exception.SkillNotFoundException;
import org.yvl.teamforge.skill.exception.UserSkillAlreadyExistsException;
import org.yvl.teamforge.skill.exception.UserSkillNotFoundException;
import org.yvl.teamforge.team.exception.TeamMemberAccessDeniedException;
import org.yvl.teamforge.team.exception.TeamMemberAlreadyInactiveException;
import org.yvl.teamforge.team.exception.TeamMemberNotFoundException;
import org.yvl.teamforge.team.exception.TeamNotFoundException;
import org.yvl.teamforge.team.exception.UserAlreadyTeamMemberException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            InvalidRefreshTokenException.class,
            InvalidCredentialsException.class
    })
    public ApiError handleUnauthorized(RuntimeException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidAuthenticatedPrincipal.class)
    public ApiError handleInvalidAuthenticatedPrincipal(InvalidAuthenticatedPrincipal exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            SystemRoleNotFoundException.class,
            SkillCategoryNotFoundException.class
    })
    public ApiError handleInternalServerError(RuntimeException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({
            UserBlockedException.class,
            ProjectAccessDeniedException.class,
            InvitationAccessDeniedException.class,
            TeamMemberAccessDeniedException.class
    })
    public ApiError handleForbidden(RuntimeException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            UserNotFoundException.class,
            UserSkillNotFoundException.class,
            SkillNotFoundException.class,
            ProjectTemplateNotFoundException.class,
            ProjectNotFoundException.class,
            ProjectRoleNotFoundException.class,
            ProjectRoleNotBelongToProjectException.class,
            ProjectRoleSkillNotFoundException.class,
            TeamNotFoundException.class,
            InvitationNotFoundException.class,
            TeamMemberNotFoundException.class,
            TeamAnalyticsNotFoundException.class
    })
    public ApiError handleNotFound(RuntimeException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
            AdminTargetModificationNotAllowedException.class,
            UserSkillAlreadyExistsException.class,
            SkillAlreadyExistsException.class,
            InvalidProjectStatusTransitionException.class,
            ProjectArchivedException.class,
            ProjectRoleSkillAlreadyExistsException.class,
            ProjectRoleNotOpenException.class,
            ProjectRoleHasTeamMembersException.class,
            ProjectRoleHasNoSkillRequirementsException.class,
            InvitationAlreadyExistsException.class,
            UserAlreadyTeamMemberException.class,
            InvitationAlreadyRespondedException.class,
            TeamMemberAlreadyInactiveException.class,
            EmailAlreadyExistsException.class
    })
    public ApiError handleConflict(RuntimeException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation error");

        return new ApiError(message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ApiError handleMethodValidation() {
        return new ApiError("Validation error");
    }
}
