package org.yvl.authenticationservice.refreshToken.service;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yvl.authenticationservice.config.JwtProperties;
import org.yvl.authenticationservice.entity.RefreshToken;
import org.yvl.authenticationservice.entity.User;
import org.yvl.authenticationservice.refreshToken.exception.InvalidRefreshTokenException;
import org.yvl.authenticationservice.repository.RefreshTokenRepository;
import org.yvl.authenticationservice.security.hash.HashService;
import org.yvl.authenticationservice.security.jwt.key.JwtKey;
import org.yvl.authenticationservice.security.jwt.service.JwtService;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    private static final String KID = "test-key-1";
    private static final long ACCESS_EXPIRATION = 900_000L;
    private static final long REFRESH_EXPIRATION = 604_800_000L;

    @Mock
    private RefreshTokenRepository repository;

    @Mock
    private RefreshTokenRevocationService refreshTokenRevocationService;

    private final HashService hashService = new HashService();

    private final KeyPair keyPair = generateKeyPair();

    @Test
    void validRefreshTokenIsAccepted() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        String token = jwtService.generateRefreshToken("user@test.com");
        RefreshToken stored = storedToken(jwtService, token);

        when(repository.findByJti(stored.getJti())).thenReturn(Optional.of(stored));

        RefreshToken result = service.validate(token);

        assertSame(stored, result);
        verifyNoInteractions(refreshTokenRevocationService);
    }

    @Test
    void tokenWithUnknownKidIsRejectedBeforeRepositoryLookup() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        String token = signToken(keyPair.getPrivate(), "unknown-key");

        assertThrows(InvalidRefreshTokenException.class, () -> service.validate(token));

        verifyNoInteractions(repository, refreshTokenRevocationService);
    }

    @Test
    void tokenWithoutKidIsRejectedBeforeRepositoryLookup() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        String token = signToken(keyPair.getPrivate(), null);

        assertThrows(InvalidRefreshTokenException.class, () -> service.validate(token));

        verifyNoInteractions(repository, refreshTokenRevocationService);
    }

    @Test
    void tokenWithInvalidSignatureIsRejectedBeforeRepositoryLookup() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        String token = signToken(generateKeyPair().getPrivate(), KID);

        assertThrows(InvalidRefreshTokenException.class, () -> service.validate(token));

        verifyNoInteractions(repository, refreshTokenRevocationService);
    }

    @Test
    void malformedTokenIsRejectedBeforeRepositoryLookup() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        assertThrows(InvalidRefreshTokenException.class, () -> service.validate("not-a-jwt"));
        assertThrows(InvalidRefreshTokenException.class, () -> service.validate("a.b.c"));

        verifyNoInteractions(repository, refreshTokenRevocationService);
    }

    @Test
    void expiredTokenIsRejectedBeforeRepositoryLookup() {
        JwtService jwtService = createJwtService(-1_000L);
        RefreshTokenService service = createService(jwtService);

        String token = jwtService.generateRefreshToken("user@test.com");

        assertThrows(InvalidRefreshTokenException.class, () -> service.validate(token));

        verifyNoInteractions(repository, refreshTokenRevocationService);
    }

    @Test
    void unknownJtiIsRejected() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        String token = jwtService.generateRefreshToken("user@test.com");

        when(repository.findByJti(jwtService.getJti(token))).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshTokenException.class, () -> service.validate(token));
    }

    @Test
    void revokedTokenRevokesAllUserTokensAndIsRejected() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        String token = jwtService.generateRefreshToken("user@test.com");
        RefreshToken stored = storedToken(jwtService, token);
        stored.setRevoked(true);

        when(repository.findByJti(stored.getJti())).thenReturn(Optional.of(stored));

        assertThrows(InvalidRefreshTokenException.class, () -> service.validate(token));

        verify(refreshTokenRevocationService).revokeAll(stored.getUser());
    }

    @Test
    void tokenWithHashMismatchIsRejected() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        String token = jwtService.generateRefreshToken("user@test.com");
        RefreshToken stored = storedToken(jwtService, token);
        stored.setTokenHash(hashService.sha256("another-token"));

        when(repository.findByJti(stored.getJti())).thenReturn(Optional.of(stored));

        assertThrows(InvalidRefreshTokenException.class, () -> service.validate(token));

        verifyNoInteractions(refreshTokenRevocationService);
    }

    @Test
    void tokenExpiredInStorageIsRejected() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        String token = jwtService.generateRefreshToken("user@test.com");
        RefreshToken stored = storedToken(jwtService, token);
        stored.setExpiresAt(Instant.now().minusSeconds(60));

        when(repository.findByJti(stored.getJti())).thenReturn(Optional.of(stored));

        assertThrows(InvalidRefreshTokenException.class, () -> service.validate(token));
    }

    @Test
    void savePersistsTokenDataFromJwt() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        User user = new User();
        user.setId(7L);

        String token = jwtService.generateRefreshToken("user@test.com");

        service.save(user, token);

        verify(repository).save(argThat(saved ->
                jwtService.getJti(token).equals(saved.getJti())
                        && hashService.sha256(token).equals(saved.getTokenHash())
                        && jwtService.getExpiration(token).equals(saved.getExpiresAt())
                        && !saved.isRevoked()
                        && saved.getUser() == user
                        && Long.valueOf(7L).equals(saved.getUserId())
        ));
    }

    @Test
    void revokeRefreshTokenRevokesMatchingToken() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        String token = jwtService.generateRefreshToken("user@test.com");
        RefreshToken stored = storedToken(jwtService, token);

        when(repository.findByJti(stored.getJti())).thenReturn(Optional.of(stored));

        service.revokeRefreshToken(token);

        assertTrue(stored.isRevoked());
    }

    @Test
    void revokeRefreshTokenIgnoresInvalidJwt() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        assertDoesNotThrow(() -> service.revokeRefreshToken("not-a-jwt"));

        verify(repository, never()).findByJti(any());
    }

    @Test
    void revokeRefreshTokenIgnoresTokenWithUnknownKid() {
        JwtService jwtService = createJwtService(REFRESH_EXPIRATION);
        RefreshTokenService service = createService(jwtService);

        String token = signToken(keyPair.getPrivate(), "unknown-key");

        assertDoesNotThrow(() -> service.revokeRefreshToken(token));

        verify(repository, never()).findByJti(any());
    }

    private RefreshTokenService createService(JwtService jwtService) {
        return new RefreshTokenService(
                hashService,
                jwtService,
                refreshTokenRevocationService,
                repository
        );
    }

    private RefreshToken storedToken(JwtService jwtService, String token) {
        User user = new User();
        user.setId(7L);

        return RefreshToken.builder()
                .tokenHash(hashService.sha256(token))
                .jti(jwtService.getJti(token))
                .createdAt(Instant.now())
                .expiresAt(jwtService.getExpiration(token))
                .revoked(false)
                .user(user)
                .userId(user.getId())
                .build();
    }

    private JwtService createJwtService(long refreshExpiration) {
        JwtProperties properties = new JwtProperties();
        properties.setAccessExpiration(ACCESS_EXPIRATION);
        properties.setRefreshExpiration(refreshExpiration);

        return new JwtService(new JwtKey(KID, keyPair.getPublic(), keyPair.getPrivate()), properties);
    }

    private String signToken(PrivateKey privateKey, String kid) {
        var builder = Jwts.builder()
                .subject("user@test.com")
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + RefreshTokenServiceTest.REFRESH_EXPIRATION));

        if (kid != null) {
            builder.header().keyId(kid);
        }

        return builder
                .signWith(privateKey, Jwts.SIG.ES256)
                .compact();
    }

    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(new ECGenParameterSpec("secp256r1"));
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate test key pair", e);
        }
    }

}
