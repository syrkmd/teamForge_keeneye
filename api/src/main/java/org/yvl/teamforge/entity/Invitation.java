package org.yvl.teamforge.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yvl.teamforge.entity.enums.InvitationStatus;

import java.time.Instant;

@Entity
@Table(name = "invitations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_role_id", nullable = false)
    private ProjectRole projectRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
