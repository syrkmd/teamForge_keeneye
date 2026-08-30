package org.yvl.teamforge.admin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.yvl.teamforge.admin.dto.response.UserAdminView;
import org.yvl.teamforge.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "systemRole.name", target = "role")
    UserAdminView toAdminView(User user);
}
