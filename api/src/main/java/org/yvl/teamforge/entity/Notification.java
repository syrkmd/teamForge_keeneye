package org.yvl.teamforge.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yvl.teamforge.entity.enums.NotificationType;

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

    @Column(name = "is_read",nullable = false)
    private Boolean isRead;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_invitation_id", insertable = false, updatable = false)
    private Invitation invitation;

    @Column(name = "related_invitation_id")
    private Long invitationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_team_id", insertable = false, updatable = false)
    private Team team;

    @Column(name = "related_team_id")
    private Long teamId;
}
