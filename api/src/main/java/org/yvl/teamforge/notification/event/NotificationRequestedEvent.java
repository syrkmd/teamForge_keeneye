package org.yvl.teamforge.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.yvl.teamforge.entity.enums.NotificationType;

@Data
@AllArgsConstructor
public class NotificationRequestedEvent {

    private Long notificationId;
    private Long userId;
    private NotificationType type;
}
