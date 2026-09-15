package org.yvl.teamforge.analytics.exception;

public class TeamAnalyticsNotFoundException extends RuntimeException {
    public TeamAnalyticsNotFoundException() {
        super("The analytics have not yet been calculated");
    }
}
