package org.yvl.teamforge.exception;

public class AdminTargetModificationNotAllowedException extends RuntimeException {
    public AdminTargetModificationNotAllowedException() {
        super("Cannot modify another administrator");
    }
}
