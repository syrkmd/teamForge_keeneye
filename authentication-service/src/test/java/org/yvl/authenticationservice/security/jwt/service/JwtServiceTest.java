package org.yvl.authenticationservice.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.yvl.authenticationservice.config.JwtProperties;
import org.yvl.authenticationservice.security.jwt.key.JwtKey;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String KID = "test-key-1";
    private static final long ACCESS_EXPIRATION = 900_000L;
    private static final long REFRESH_EXPIRATION = 604_800_000L;

    private final KeyPair keyPair = generateKeyPair();

    @Test
    void accessTokenIsSignedWithEs256AndContainsKid() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = jwtService.generateAccessToken("user@test.com", 123L, "USER");

        Jws<Claims> jws = parse(token);

        assertEquals("ES256", jws.getHeader().getAlgorithm());
        assertEquals(KID, jws.getHeader().getKeyId());
    }

    @Test
    void accessTokenSignatureIsEs256Signature() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = jwtService.generateAccessToken("user@test.com", 123L, "USER");

        byte[] signature = Base64.getUrlDecoder().decode(token.split("\\.")[2]);

        assertEquals(64, signature.length);
    }

    @Test
    void accessTokenContainsExactlyContractClaims() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = jwtService.generateAccessToken("user@test.com", 123L, "USER");

        Claims claims = parse(token).getPayload();

        assertEquals(Set.of("sub", "userId", "role", "iat", "exp"), claims.keySet());
        assertEquals("user@test.com", claims.getSubject());
        assertEquals(123L, claims.get("userId", Long.class));
        assertEquals("USER", claims.get("role", String.class));
        assertLifetime(claims, ACCESS_EXPIRATION);
    }

    @Test
    void refreshTokenIsSignedWithEs256AndContainsKid() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = jwtService.generateRefreshToken("user@test.com");

        Jws<Claims> jws = parse(token);

        assertEquals("ES256", jws.getHeader().getAlgorithm());
        assertEquals(KID, jws.getHeader().getKeyId());
    }

    @Test
    void refreshTokenContainsExactlyContractClaims() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = jwtService.generateRefreshToken("user@test.com");

        Claims claims = parse(token).getPayload();

        assertEquals(Set.of("sub", "jti", "iat", "exp"), claims.keySet());
        assertEquals("user@test.com", claims.getSubject());
        assertDoesNotThrow(() -> UUID.fromString(claims.getId()));
        assertLifetime(claims, REFRESH_EXPIRATION);
    }

    @Test
    void refreshTokensHaveUniqueJti() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String first = jwtService.generateRefreshToken("user@test.com");
        String second = jwtService.generateRefreshToken("user@test.com");

        assertNotEquals(jwtService.getJti(first), jwtService.getJti(second));
    }

    @Test
    void validRefreshTokenIsAccepted() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = jwtService.generateRefreshToken("user@test.com");

        Claims claims = jwtService.getClaims(token);

        assertEquals("user@test.com", claims.getSubject());
        assertEquals(claims.getId(), jwtService.getJti(token));
        assertEquals(
                Instant.ofEpochSecond(claims.getExpiration().toInstant().getEpochSecond()),
                jwtService.getExpiration(token)
        );
    }

    @Test
    void tokenWithInvalidSignatureIsRejected() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = signToken(generateKeyPair().getPrivate(), KID);

        assertThrows(SignatureException.class, () -> jwtService.getClaims(token));
    }

    @Test
    void tokenWithTamperedPayloadIsRejected() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = jwtService.generateAccessToken("user@test.com", 123L, "USER");

        String[] parts = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String tamperedPayload = payload.replace("\"role\":\"USER\"", "\"role\":\"ADMIN\"");

        assertNotEquals(payload, tamperedPayload);

        String tamperedToken = parts[0] + "."
                + Base64.getUrlEncoder().withoutPadding().encodeToString(tamperedPayload.getBytes(StandardCharsets.UTF_8))
                + "." + parts[2];

        assertThrows(SignatureException.class, () -> jwtService.getClaims(tamperedToken));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService jwtService = createJwtService(keyPair, -1_000L);

        String token = jwtService.generateRefreshToken("user@test.com");

        assertThrows(ExpiredJwtException.class, () -> jwtService.getClaims(token));
    }

    @Test
    void tokenWithUnexpectedKidIsRejected() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = signToken(keyPair.getPrivate(), "unknown-key");

        assertThrows(JwtException.class, () -> jwtService.getClaims(token));
    }

    @Test
    void tokenWithoutKidIsRejected() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = signToken(keyPair.getPrivate(), null);

        assertThrows(JwtException.class, () -> jwtService.getClaims(token));
    }

    @Test
    void unsignedTokenIsRejected() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = Jwts.builder()
                .subject("user@test.com")
                .id(UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .header().keyId(KID).and()
                .compact();

        assertThrows(JwtException.class, () -> jwtService.getClaims(token));
    }

    @Test
    void tokenSignedWithHs256UsingPublicKeyAsSecretIsRejected() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = Jwts.builder()
                .subject("user@test.com")
                .id(UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .header().keyId(KID).and()
                .signWith(Keys.hmacShaKeyFor(keyPair.getPublic().getEncoded()), Jwts.SIG.HS256)
                .compact();

        assertThrows(JwtException.class, () -> jwtService.getClaims(token));
    }

    @Test
    void malformedTokenIsRejected() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        assertThrows(JwtException.class, () -> jwtService.getClaims("not-a-jwt"));
        assertThrows(JwtException.class, () -> jwtService.getClaims("a.b.c"));
        assertThrows(JwtException.class, () -> jwtService.getClaims("e30.e30.e30"));
    }

    @Test
    void tokenWithTruncatedSignatureIsRejected() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = jwtService.generateRefreshToken("user@test.com");
        String truncatedToken = token.substring(0, token.length() - 10);

        assertThrows(JwtException.class, () -> jwtService.getClaims(truncatedToken));
    }

    @Test
    void emptyTokenIsRejectedWithIllegalArgumentException() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        assertThrows(IllegalArgumentException.class, () -> jwtService.getClaims(""));
    }



    @Test
    void tokenWithUnknownKidAndInvalidSignatureIsRejected() {
        JwtService jwtService = createJwtService(keyPair, REFRESH_EXPIRATION);

        String token = signToken(generateKeyPair().getPrivate(), "unknown-key");

        assertThrows(JwtException.class, () -> jwtService.getClaims(token));
    }

    private JwtService createJwtService(KeyPair keyPair, long refreshExpiration) {
        JwtProperties properties = new JwtProperties();
        properties.setAccessExpiration(JwtServiceTest.ACCESS_EXPIRATION);
        properties.setRefreshExpiration(refreshExpiration);

        return new JwtService(new JwtKey(KID, keyPair.getPublic(), keyPair.getPrivate()), properties);
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token);
    }

    private String signToken(PrivateKey privateKey, String kid) {
        var builder = Jwts.builder()
                .subject("user@test.com")
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JwtServiceTest.REFRESH_EXPIRATION));

        if (kid != null) {
            builder.header().keyId(kid);
        }

        return builder
                .signWith(privateKey, Jwts.SIG.ES256)
                .compact();
    }

    private static void assertLifetime(Claims claims, long expectedMillis) {
        long actualSeconds = claims.getExpiration().toInstant().getEpochSecond()
                - claims.getIssuedAt().toInstant().getEpochSecond();

        assertTrue(Math.abs(actualSeconds - expectedMillis / 1000) <= 1);
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
