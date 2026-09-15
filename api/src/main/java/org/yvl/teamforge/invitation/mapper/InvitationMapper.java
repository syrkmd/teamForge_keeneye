package org.yvl.teamforge.invitation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.yvl.teamforge.entity.Invitation;
import org.yvl.teamforge.invitation.dto.response.InvitationView;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "projectRole.id", target = "projectRoleId")
    @Mapping(source = "user.id", target = "userId")
    InvitationView toInvitationView(Invitation invitation);
}
