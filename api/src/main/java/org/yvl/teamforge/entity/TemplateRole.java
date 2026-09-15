package org.yvl.teamforge.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "template_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "required_count", nullable = false)
    private Integer requiredCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ProjectTemplate projectTemplate;
}
