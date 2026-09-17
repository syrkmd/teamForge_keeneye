package org.yvl.teamforge.auth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.auth.dto.request.LoginRequest;
import org.yvl.teamforge.auth.dto.request.RefreshTokenRequest;
import org.yvl.teamforge.auth.dto.request.RegisterRequest;
import org.yvl.teamforge.auth.dto.response.AuthResponse;
import org.yvl.teamforge.auth.dto.response.RefreshTokenResponse;
import org.yvl.teamforge.entity.RefreshToken;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.entity.enums.SystemRoleName;
import org.yvl.teamforge.auth.exception.EmailAlreadyExistsException;
import org.yvl.teamforge.auth.exception.InvalidAuthenticatedPrincipal;
import org.yvl.teamforge.auth.exception.InvalidCredentialsException;
import org.yvl.teamforge.auth.exception.UserBlockedException;
import org.yvl.teamforge.exception.SystemRoleNotFoundException;
import org.yvl.teamforge.repository.SystemRoleRepository;
import org.yvl.teamforge.repository.UserRepository;
import org.yvl.teamforge.security.jwt.service.JwtService;
import org.yvl.teamforge.security.user.UserPrincipal;
import org.yvl.teamforge.refreshToken.service.RefreshTokenService;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SystemRoleRepository systemRoleRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

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
                        .systemRole(
                                systemRoleRepository.findByName(SystemRoleName.USER).orElseThrow(() -> new SystemRoleNotFoundException(SystemRoleName.USER))
                        )
                        .build()
        );


        UserPrincipal userPrincipal = new UserPrincipal(user);

        String accessToken = jwtService.generateAccessToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        refreshTokenService.save(user, refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            Object principal = authentication.getPrincipal();

            if (!(principal instanceof UserPrincipal userPrincipal)) {
                throw new InvalidAuthenticatedPrincipal();
            }

            String accessToken = jwtService.generateAccessToken(userPrincipal);
            String refreshToken = jwtService.generateRefreshToken(userPrincipal);

            refreshTokenService.save(userPrincipal.getUser(), refreshToken);

            return new AuthResponse(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (DisabledException e) {
            throw new UserBlockedException("User account is blocked");
        }
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenService.validate(request.getRefreshToken());

        token.setRevoked(true);

        User user = token.getUser();

        UserPrincipal userPrincipal = new UserPrincipal(user);

        String accessToken = jwtService.generateAccessToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        refreshTokenService.save(user, refreshToken);

        return new RefreshTokenResponse(accessToken, refreshToken);
    }

    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
    }
}
