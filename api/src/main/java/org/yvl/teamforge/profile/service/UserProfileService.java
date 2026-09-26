package org.yvl.teamforge.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.exception.UserNotFoundException;
import org.yvl.teamforge.profile.dto.request.ProfileUpdateRequest;
import org.yvl.teamforge.profile.dto.response.UserProfileView;
import org.yvl.teamforge.profile.mapper.UserProfileMapper;
import org.yvl.teamforge.repository.UserRepository;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final UserRepository userRepository;

    public UserProfileView getProfile(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getUser().getId()).orElseThrow(() ->
                new UserNotFoundException(userPrincipal.getUser().getId()));

        return userProfileMapper.toProfileView(user);
    }

    public UserProfileView profileUpdate(UserPrincipal userPrincipal, ProfileUpdateRequest request) {

        User user = userRepository.findById(userPrincipal.getUser().getId()).orElseThrow(() ->
                new UserNotFoundException(userPrincipal.getUser().getId()));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getAbout() != null) {
            user.setAbout(request.getAbout());
        }

        if (request.getGithubUsername() != null) {
            user.setGithubUsername(request.getGithubUsername());
        }

        user.setUpdatedAt(Instant.now());

        return userProfileMapper.toProfileView(user);
    }
}
