package org.yvl.teamforge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "template_role_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_template_role_skill",
                        columnNames = {
                                "template_role_id",
                                "skill_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRoleSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_level", nullable = false)
    private Integer minLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_role_id", nullable = false)
    private TemplateRole templateRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}
