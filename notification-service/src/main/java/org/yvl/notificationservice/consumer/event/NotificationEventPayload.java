package org.yvl.notificationservice.consumer.event;

import lombok.Data;

@Data
public class NotificationEventPayload {

    private Long notificationId;
    private Long userId;
}
