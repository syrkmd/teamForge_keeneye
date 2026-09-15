package org.yvl.teamforge.refreshToken.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Refresh token is invalid");
    }
}