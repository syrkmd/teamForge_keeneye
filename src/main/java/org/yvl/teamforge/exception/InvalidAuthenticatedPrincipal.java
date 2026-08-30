package org.yvl.teamforge.exception;

public class InvalidAuthenticatedPrincipal extends RuntimeException {
    public InvalidAuthenticatedPrincipal() {
        super("Authenticated principal is invalid");
    }
}
