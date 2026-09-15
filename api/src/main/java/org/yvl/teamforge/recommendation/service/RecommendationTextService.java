package org.yvl.teamforge.recommendation.service;

import org.springframework.stereotype.Service;
import org.yvl.teamforge.analytics.dto.projection.SkillGapRow;
import org.yvl.teamforge.entity.enums.RiskLevel;

import java.util.List;


@Service
public class RecommendationTextService {

    public String generateTitle(RiskLevel riskLevel, List<SkillGapRow> skillGap) {
        if (!skillGap.isEmpty()) {
            return switch (riskLevel) {
                case LOW -> "Дефицит навыков";
                case MEDIUM -> "Средний риск: дефицит навыков";
                case HIGH -> "Высокий риск: дефицит навыков";
            };
        }

        return switch (riskLevel) {
            case LOW -> "Команда в хорошем состоянии";
            case MEDIUM -> "Средний риск команды";
            case HIGH -> "Высокий риск команды";
        };
    }

    public String generateDescription(
            RiskLevel riskLevel,
            List<SkillGapRow> skillGap
    ) {
        StringBuilder description = new StringBuilder();

        description
                .append("Текущий уровень риска команды: ")
                .append(riskLevel)
                .append(".");

        if (!skillGap.isEmpty()) {
            description.append(" Обнаружены дефициты навыков:");

            for (SkillGapRow row : skillGap) {
                description
                        .append(" ")
                        .append(row.getRoleName())
                        .append(" — ")
                        .append(row.getSkillName())
                        .append(": требуется ")
                        .append(row.getMinLevel())
                        .append(", фактически ")
                        .append(row.getActualLevel())
                        .append(".");
            }
        }

        return description.toString();
    }
}