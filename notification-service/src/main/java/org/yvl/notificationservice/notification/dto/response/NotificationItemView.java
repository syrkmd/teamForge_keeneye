package org.yvl.notificationservice.notification.dto.response;

import lombok.Data;
import org.yvl.notificationservice.entity.enums.NotificationType;

import java.time.Instant;

@Data
public class NotificationItemView {

    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Boolean isRead;
    private Instant createdAt;
    private Long relatedInvitationId;
    private Long relatedTeamId;
}
