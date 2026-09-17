package org.yvl.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yvl.notificationservice.entity.enums.NotificationType;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "related_invitation_id")
    private Long relatedInvitationId;

    @Column(name = "related_team_id")
    private Long relatedTeamId;
}
