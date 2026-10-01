package org.yvl.teamforge.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yvl.teamforge.entity.enums.TypeSkill;

@Entity
@Table(
        name = "skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_skill_category_name",
                        columnNames = {
                                "category_id",
                                "name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeSkill type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, insertable = false, updatable = false)
    private SkillCategory category;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", insertable = false, updatable = false)
    private User createdByUser;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;
}
