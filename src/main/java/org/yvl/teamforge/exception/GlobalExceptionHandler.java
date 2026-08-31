package org.yvl.teamforge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.yvl.teamforge.exception.dto.ApiError;
import org.yvl.teamforge.exception.dto.StatusCode;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ApiError handleUsernameAlreadyExists(EmailAlreadyExistsException exception) {
        return new ApiError(exception.getMessage(), StatusCode.EMAIL_ALREADY_EXISTS);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ApiError handleInvalidRefreshToken(InvalidRefreshTokenException exception) {
        return new ApiError(exception.getMessage(), StatusCode.INVALID_REFRESH_TOKEN);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidAuthenticatedPrincipal.class)
    public ApiError handleInvalidAuthenticatedPrincipal(InvalidAuthenticatedPrincipal exception) {
        return new ApiError(exception.getMessage(), StatusCode.INVALID_AUTH_PRINCIPAL);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SystemRoleNotFoundException.class)
    public ApiError handleSystemRoleNotFoundException(SystemRoleNotFoundException exception) {
        return new ApiError(exception.getMessage(), StatusCode.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SkillCategoryNotFoundException.class)
    public ApiError handleSkillCategoryNotFoundException(SkillCategoryNotFoundException exception) {
        return new ApiError(exception.getMessage(), StatusCode.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ApiError handleInvalidCredentialsException(InvalidCredentialsException exception) {
        return new ApiError(exception.getMessage(), StatusCode.INVALID_CREDENTIALS);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UserBlockedException.class)
    public ApiError handleUserBlockedException(UserBlockedException exception) {
        return new ApiError(exception.getMessage(), StatusCode.USER_BLOCKED);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ApiError handleUserNotFound(UserNotFoundException exception) {
        return new ApiError(exception.getMessage(), StatusCode.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserSkillNotFoundException.class)
    public ApiError handleUserSkillNotFound(UserSkillNotFoundException exception) {
        return new ApiError(exception.getMessage(), StatusCode.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SkillNotFoundException.class)
    public ApiError handleSkillNotFound(SkillNotFoundException exception) {
        return new ApiError(exception.getMessage(), StatusCode.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(AdminTargetModificationNotAllowedException.class)
    public ApiError handleAdminTargetModificationNotAllowedException(AdminTargetModificationNotAllowedException exception) {
        return new ApiError(exception.getMessage(), StatusCode.ADMIN_MODIFICATION_NOT_ALLOWED);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserSkillAlreadyExistsException.class)
    public ApiError handleUserSkillAlreadyExistsException(UserSkillAlreadyExistsException exception) {
        return new ApiError(exception.getMessage(), StatusCode.USER_SKILL_ALREADY_EXISTS);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SkillAlreadyExistsException.class)
    public ApiError handleSkillAlreadyExists(SkillAlreadyExistsException exception) {
        return new ApiError(exception.getMessage(), StatusCode.SKILL_ALREADY_EXISTS);
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

        return new ApiError(message, StatusCode.VALIDATION_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ApiError handleMethodValidation(HandlerMethodValidationException exception) {
        return new ApiError("Validation error", StatusCode.VALIDATION_ERROR);
    }
}
