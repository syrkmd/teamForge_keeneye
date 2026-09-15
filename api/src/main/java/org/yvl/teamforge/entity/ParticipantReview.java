package org.yvl.teamforge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "participant_reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reviewer_reviewee",
                        columnNames = {
                                "reviewer_id",
                                "reviewee_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private TeamMember reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id")
    private TeamMember reviewee;
}
