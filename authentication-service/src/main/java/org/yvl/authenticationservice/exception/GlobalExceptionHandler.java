package org.yvl.authenticationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.yvl.authenticationservice.auth.exception.EmailAlreadyExistsException;
import org.yvl.authenticationservice.auth.exception.InvalidCredentialsException;
import org.yvl.authenticationservice.auth.exception.UserBlockedException;
import org.yvl.authenticationservice.exception.dto.ApiError;
import org.yvl.authenticationservice.refreshToken.exception.InvalidRefreshTokenException;

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

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UserBlockedException.class)
    public ApiError handleForbidden(UserBlockedException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ApiError handleConflict(EmailAlreadyExistsException exception) {
        return new ApiError(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SystemRoleNotFoundException.class)
    public ApiError handleInternalServerError(SystemRoleNotFoundException exception) {
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
