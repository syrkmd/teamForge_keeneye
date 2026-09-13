package org.yvl.teamforge.auth.exception;

public class InvalidAuthenticatedPrincipal extends RuntimeException {
    public InvalidAuthenticatedPrincipal() {
        super("Authenticated principal is invalid");
    }
}
