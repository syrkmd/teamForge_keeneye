package org.yvl.teamforge.project.exception;

public class ProjectTemplateNotFoundException extends RuntimeException {
    public ProjectTemplateNotFoundException(Long id) {
        super("Could not find project template with id: " + id);
    }
}
