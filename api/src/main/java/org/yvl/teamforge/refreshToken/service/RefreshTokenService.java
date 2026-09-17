package org.yvl.teamforge.refreshToken.service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.RefreshToken;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.refreshToken.exception.InvalidRefreshTokenException;
import org.yvl.teamforge.repository.RefreshTokenRepository;
import org.yvl.teamforge.security.hash.HashService;
import org.yvl.teamforge.security.jwt.service.JwtService;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final HashService hashService;
    private final JwtService jwtService;
    private final RefreshTokenRevocationService refreshTokenRevocationService;
    private final RefreshTokenRepository repository;


    public void save(User user, String refreshToken) {
        String jti = jwtService.getJti(refreshToken);
        String tokenHash = hashService.sha256(refreshToken);

        RefreshToken token = RefreshToken.builder()
                .tokenHash(tokenHash)
                .jti(jti)
                .createdAt(Instant.now())
                .expiresAt(jwtService.getExpiration(refreshToken))
                .revoked(false)
                .user(user)
                .build();

        repository.save(token);
    }

    public void revokeRefreshToken(String refreshToken) {
        try {
            String jti = jwtService.getJti(refreshToken);

            repository
                    .findByJti(jti)
                    .ifPresent(token -> {
                        if (token.getTokenHash().equals(hashService.sha256(refreshToken))) {
                            token.setRevoked(true);
                        }
                    });

        } catch (JwtException e) {
            log.debug("Invalid JWT logout");
        }
    }

    public RefreshToken validate(String refreshToken) {
        try {

            String jti = jwtService.getJti(refreshToken);

            RefreshToken token = repository.findByJti(jti)
                    .orElseThrow(InvalidRefreshTokenException::new);

            if (token.isRevoked()) {
                refreshTokenRevocationService.revokeAll(token.getUser());
                throw new InvalidRefreshTokenException();
            }

            if (!token.getTokenHash().equals(hashService.sha256(refreshToken))) {
                throw new InvalidRefreshTokenException();
            }

            if (token.getExpiresAt().isBefore(Instant.now())) {
                throw new InvalidRefreshTokenException();
            }

            return token;
        } catch (JwtException e) {
            throw new InvalidRefreshTokenException();
        }
    }
}
