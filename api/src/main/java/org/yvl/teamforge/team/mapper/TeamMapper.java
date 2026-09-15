package org.yvl.teamforge.team.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.yvl.teamforge.entity.Team;
import org.yvl.teamforge.entity.TeamMember;
import org.yvl.teamforge.team.dto.response.TeamMemberView;
import org.yvl.teamforge.team.dto.response.TeamView;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(source = "project.id", target = "projectId")
    TeamView toTeamView(Team team);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "projectRole.id", target = "projectRoleId")
    TeamMemberView toTeamMemberView(TeamMember teamMember);
}
