package org.yvl.teamforge.exception;

public class ProjectAccessDeniedException extends RuntimeException {
    public ProjectAccessDeniedException() {
        super("You are not allowed to update this project");
    }
}
