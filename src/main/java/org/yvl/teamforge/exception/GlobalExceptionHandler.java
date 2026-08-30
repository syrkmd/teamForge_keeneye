package org.yvl.teamforge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
    public ApiError handleSystemRoleNotFoundException(
            SystemRoleNotFoundException exception
    ) {
        return new ApiError(
                exception.getMessage(),
                StatusCode.INTERNAL_SERVER_ERROR
        );
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
}
