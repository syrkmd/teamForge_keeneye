package org.yvl.teamforge.admin.exception;

public class AdminTargetModificationNotAllowedException extends RuntimeException {
    public AdminTargetModificationNotAllowedException() {
        super("Cannot modify another administrator");
    }
}
