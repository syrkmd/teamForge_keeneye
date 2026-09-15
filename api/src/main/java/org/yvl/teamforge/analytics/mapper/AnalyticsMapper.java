package org.yvl.teamforge.analytics.mapper;

import org.mapstruct.Mapper;
import org.yvl.teamforge.analytics.dto.response.TeamAnalyticsView;
import org.yvl.teamforge.entity.TeamAnalyticsMaterializedView;

@Mapper(componentModel = "spring")
public interface AnalyticsMapper {

    TeamAnalyticsView toTeamAnalyticsView(TeamAnalyticsMaterializedView teamAnalyticsMaterializedView);
}
