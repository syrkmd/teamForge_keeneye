package org.yvl.teamforge.exception;

public class ProjectArchivedException extends RuntimeException {
    public ProjectArchivedException() {
        super("Archived project cannot be modified");
    }
}
