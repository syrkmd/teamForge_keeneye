package org.yvl.teamforge.notification.event;

import lombok.Data;

@Data
public class NotificationEventPayload {

    private Long notificationId;
    private Long userId;
}
