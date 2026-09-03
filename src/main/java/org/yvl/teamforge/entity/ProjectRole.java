package org.yvl.teamforge.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yvl.teamforge.entity.enums.ProjectRoleStatus;

@Entity
@Table(name = "project_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "required_count", nullable = false)
    private Integer requiredCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectRoleStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
