package org.yvl.teamforge.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yvl.teamforge.entity.enums.RiskLevel;

import java.math.BigDecimal;

@Entity
@Table(name = "team_analytics")
@Getter
@NoArgsConstructor
public class TeamAnalyticsMaterializedView {

    @Id
    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "team_health")
    private BigDecimal teamHealth;

    @Column(name = "skill_coverage")
    private BigDecimal skillCoverage;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;
}
