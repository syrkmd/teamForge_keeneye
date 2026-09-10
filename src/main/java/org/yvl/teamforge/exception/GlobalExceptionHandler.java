package org.yvl.teamforge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.yvl.teamforge.exception.dto.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ApiError handleUsernameAlreadyExists(EmailAlreadyExistsException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ApiError handleInvalidRefreshToken(InvalidRefreshTokenException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidAuthenticatedPrincipal.class)
    public ApiError handleInvalidAuthenticatedPrincipal(InvalidAuthenticatedPrincipal exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SystemRoleNotFoundException.class)
    public ApiError handleSystemRoleNotFoundException(SystemRoleNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SkillCategoryNotFoundException.class)
    public ApiError handleSkillCategoryNotFoundException(SkillCategoryNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ApiError handleInvalidCredentialsException(InvalidCredentialsException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UserBlockedException.class)
    public ApiError handleUserBlockedException(UserBlockedException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ProjectAccessDeniedException.class)
    public ApiError handleProjectAccessDeniedException(ProjectAccessDeniedException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InvitationAccessDeniedException.class)
    public ApiError handleInvitationAccessDeniedException(InvitationAccessDeniedException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(TeamMemberAccessDeniedException.class)
    public ApiError handleTeamMemberAccessDeniedException(TeamMemberAccessDeniedException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ApiError handleUserNotFound(UserNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserSkillNotFoundException.class)
    public ApiError handleUserSkillNotFound(UserSkillNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SkillNotFoundException.class)
    public ApiError handleSkillNotFound(SkillNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProjectTemplateNotFoundException.class)
    public ApiError handleProjectTemplateNotFound(ProjectTemplateNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProjectNotFoundException.class)
    public ApiError handleProjectNotFound(ProjectNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProjectRoleNotFoundException.class)
    public ApiError handleProjectRoleNotFound(ProjectRoleNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProjectRoleNotBelongToProjectException.class)
    public ApiError handleProjectRoleNotBelongToProject(ProjectRoleNotBelongToProjectException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProjectRoleSkillNotFoundException.class)
    public ApiError handleProjectRoleSkillNotFound(ProjectRoleSkillNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TeamNotFoundException.class)
    public ApiError handleTeamNotFound(TeamNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InvitationNotFoundException.class)
    public ApiError handleInvitationNotFound(InvitationNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TeamMemberNotFoundException.class)
    public ApiError handleTeamMemberNotFound(TeamMemberNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TeamAnalyticsNotFoundException.class)
    public ApiError handleTeamAnalyticsNotFound(TeamAnalyticsNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(AdminTargetModificationNotAllowedException.class)
    public ApiError handleAdminTargetModificationNotAllowedException(AdminTargetModificationNotAllowedException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserSkillAlreadyExistsException.class)
    public ApiError handleUserSkillAlreadyExistsException(UserSkillAlreadyExistsException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SkillAlreadyExistsException.class)
    public ApiError handleSkillAlreadyExists(SkillAlreadyExistsException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(InvalidProjectStatusTransitionException.class)
    public ApiError handleInvalidProjectStatusTransitionException(InvalidProjectStatusTransitionException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProjectArchivedException.class)
    public ApiError handleProjectArchivedException(ProjectArchivedException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProjectRoleSkillAlreadyExistsException.class)
    public ApiError handleProjectRoleSkillAlreadyExistsException(ProjectRoleSkillAlreadyExistsException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProjectRoleNotOpenException.class)
    public ApiError handleProjectRoleNotOpenException(ProjectRoleNotOpenException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProjectRoleHasNoSkillRequirementsException.class)
    public ApiError handleProjectRoleHasNoSkillRequirements(ProjectRoleHasNoSkillRequirementsException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(InvitationAlreadyExistsException.class)
    public ApiError handleInvitationAlreadyExistsException(InvitationAlreadyExistsException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyTeamMemberException.class)
    public ApiError handleUserAlreadyTeamMemberException(UserAlreadyTeamMemberException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(InvitationAlreadyRespondedException.class)
    public ApiError handleInvitationAlreadyRespondedException(InvitationAlreadyRespondedException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(TeamMemberAlreadyInactiveException.class)
    public ApiError handleTeamMemberAlreadyInactiveException(TeamMemberAlreadyInactiveException exception) {
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
