package org.yvl.notificationservice.sse.dto;

import lombok.Data;
import org.yvl.notificationservice.entity.enums.NotificationType;

import java.time.Instant;

@Data
public class NotificationView {

    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Instant createdAt;
    private Long relatedInvitationId;
    private Long relatedTeamId;
}
