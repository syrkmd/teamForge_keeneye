package org.yvl.teamforge.security.jwt.exception;

public class JwtKeyInitializationException extends RuntimeException {
    public JwtKeyInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
