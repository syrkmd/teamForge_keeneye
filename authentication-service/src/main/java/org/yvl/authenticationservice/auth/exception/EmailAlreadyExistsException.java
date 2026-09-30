package org.yvl.authenticationservice.auth.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String username) {
        super("Email '" + username + "' already exists");
    }
}
