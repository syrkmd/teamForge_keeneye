package org.yvl.teamforge.analytics.dto.response;

import lombok.Data;
import org.yvl.teamforge.entity.enums.RiskLevel;

import java.math.BigDecimal;

@Data
public class TeamAnalyticsView {

    private Long teamId;
    private BigDecimal teamHealth;
    private BigDecimal skillCoverage;
    private RiskLevel riskLevel;
}
