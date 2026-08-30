package org.yvl.teamforge.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.admin.dto.response.UserAdminView;
import org.yvl.teamforge.admin.mapper.UserMapper;
import org.yvl.teamforge.entity.SystemRole;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.entity.enums.SystemRoleName;
import org.yvl.teamforge.exception.AdminTargetModificationNotAllowedException;
import org.yvl.teamforge.exception.SystemRoleNotFoundException;
import org.yvl.teamforge.exception.UserNotFoundException;
import org.yvl.teamforge.repository.SystemRoleRepository;
import org.yvl.teamforge.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;
    private final SystemRoleRepository systemRoleRepository;
    private final UserMapper userMapper;

    public Page<UserAdminView> getUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        return users.map(userMapper::toAdminView);
    }

    public UserAdminView changeRole(Long userId, SystemRoleName role) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        SystemRole systemRole = systemRoleRepository.findByName(role).orElseThrow(() -> new SystemRoleNotFoundException(role));

        if (user.getSystemRole().getName() == SystemRoleName.ADMIN && systemRole.getName() != SystemRoleName.ADMIN) {
            throw new AdminTargetModificationNotAllowedException();
        }

        user.setSystemRole(systemRole);

        return userMapper.toAdminView(user);
    }

    public UserAdminView changeActive(Long userId, Boolean active) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getSystemRole().getName() == SystemRoleName.ADMIN && !active) {
            throw new AdminTargetModificationNotAllowedException();
        }

        user.setIsActive(active);

        return userMapper.toAdminView(user);
    }
}
