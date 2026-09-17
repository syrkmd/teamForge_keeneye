package org.yvl.notificationservice.consumer.exception;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(Long notificationId) {
        super("Notification not found for id: " + notificationId);
    }
}
