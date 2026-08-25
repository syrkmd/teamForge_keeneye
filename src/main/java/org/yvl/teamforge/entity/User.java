package org.yvl.teamforge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "about", nullable = false)
    private String about;

    @Column(name = "github_username", nullable = false)
    private String githubUsername;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "average_rating", nullable = false)
    private Double averageRating;

    @Column(name = "reviews_count", nullable = false)
    private Integer reviewsCount;

    @Column(name = "completed_projects_count", nullable = false)
    private Integer completedProjectsCount;

    @Column(name = "completion_rate", nullable = false)
    private Double completionRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_role_id", nullable = false)
    private SystemRole systemRole;
}
