package org.yvl.teamforge.auth.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String username) {
        super("Email '" + username + "' already exists");
    }
}