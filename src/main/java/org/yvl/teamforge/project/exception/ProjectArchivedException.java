package org.yvl.teamforge.project.exception;

public class ProjectArchivedException extends RuntimeException {
    public ProjectArchivedException() {
        super("Archived project cannot be modified");
    }
}
