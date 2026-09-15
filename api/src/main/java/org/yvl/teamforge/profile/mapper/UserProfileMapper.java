package org.yvl.teamforge.profile.mapper;

import org.mapstruct.Mapper;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.profile.dto.response.UserProfileView;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileView toProfileView(User user);
}
