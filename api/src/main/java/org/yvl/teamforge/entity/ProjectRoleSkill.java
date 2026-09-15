package org.yvl.teamforge.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "project_role_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_role_skill",
                        columnNames = {
                                "project_role_id",
                                "skill_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRoleSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_level", nullable = false)
    private Integer minLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_role_id", nullable = false)
    private ProjectRole projectRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}
