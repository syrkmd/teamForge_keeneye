package org.yvl.authenticationservice.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.authenticationservice.auth.dto.request.LoginRequest;
import org.yvl.authenticationservice.auth.dto.request.RefreshTokenRequest;
import org.yvl.authenticationservice.auth.dto.request.RegisterRequest;
import org.yvl.authenticationservice.auth.dto.response.AuthResponse;
import org.yvl.authenticationservice.auth.dto.response.RefreshTokenResponse;
import org.yvl.authenticationservice.auth.exception.EmailAlreadyExistsException;
import org.yvl.authenticationservice.auth.exception.InvalidCredentialsException;
import org.yvl.authenticationservice.auth.exception.UserBlockedException;
import org.yvl.authenticationservice.entity.RefreshToken;
import org.yvl.authenticationservice.entity.User;
import org.yvl.authenticationservice.entity.enums.SystemRoleName;
import org.yvl.authenticationservice.exception.SystemRoleNotFoundException;
import org.yvl.authenticationservice.refreshToken.service.RefreshTokenService;
import org.yvl.authenticationservice.repository.SystemRoleRepository;
import org.yvl.authenticationservice.repository.UserRepository;
import org.yvl.authenticationservice.security.jwt.service.JwtService;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final SystemRoleRepository systemRoleRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        var systemRole = systemRoleRepository.findByName(SystemRoleName.USER).orElseThrow(() ->
                new SystemRoleNotFoundException(SystemRoleName.USER));

        User user = userRepository.save(
                User.builder()
                        .email(request.getEmail())
                        .passwordHash(passwordEncoder.encode(request.getPassword()))
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .about("")
                        .githubUsername("")
                        .isActive(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .averageRating(0.0)
                        .reviewsCount(0)
                        .completedProjectsCount(0)
                        .completionRate(0.0)
                        .systemRole(systemRole)
                        .systemRoleId(systemRole.getId())
                        .build()
        );

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                user.getId(),
                user.getSystemRole().getName().name()
        );

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        refreshTokenService.save(user, refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.getIsActive()) {
            throw new UserBlockedException("User account is blocked");
        }

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                user.getId(),
                user.getSystemRole().getName().name()
        );

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        refreshTokenService.save(user, refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenService.validate(request.getRefreshToken());

        token.setRevoked(true);

        User user = token.getUser();

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                user.getId(),
                user.getSystemRole().getName().name()
        );

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        refreshTokenService.save(user, refreshToken);

        return new RefreshTokenResponse(accessToken, refreshToken);
    }

    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
    }
}
