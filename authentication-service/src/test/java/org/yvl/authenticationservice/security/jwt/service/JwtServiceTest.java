package org.yvl.authenticationservice.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.yvl.authenticationservice.config.JwtProperties;

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

    private static final String GOLDEN_KID = "teamforge-test-key";
    private static final String GOLDEN_JTI = "9e96599d-6e4d-4d52-bcd0-50030994e8d7";
    private static final String GOLDEN_PUBLIC_KEY =
            "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUZrd0V3WUhLb1pJemowQ0FRWUlLb1pJemowREFRY0RRZ0FFQTU0ZDlZa0k0a1dMMTlq" +
            "TDk4eGx4OWhsNnhkZgo2UmUxeXB3MHcyazlKS211aG9vZzI5aHpCbUJVMy84elhHaERDWFJtLzBkTXZudHNVWXlvV3pWWk9BPT0KLS0t" +
            "LS1FTkQgUFVCTElDIEtFWS0tLS0tCg==";
    private static final String GOLDEN_REFRESH_TOKEN =
            "eyJraWQiOiJ0ZWFtZm9yZ2UtdGVzdC1rZXkiLCJhbGciOiJFUzI1NiJ9." +
            "eyJzdWIiOiJnb2xkZW5AdGVhbWZvcmdlLnRlc3QiLCJqdGkiOiI5ZTk2NTk5ZC02ZTRkLTRkNTItYmNkMC01MDAzMDk5NGU4ZDciLCJpYXQi" +
            "OjE3OTEwNDEyNTQsImV4cCI6NDEwMjQ0NDgwMH0." +
            "bnSZC-R3p9E7Ix217csVlmxquiCb9HDsZ2-X_dRM8lnQSNw2MOZKs9Ui9Xd8g0HUeSUh2hEjQ_xMuFUZV9lkdg";

    private final KeyPair keyPair = generateKeyPair();

    @Test
    void accessTokenIsSignedWithEs256AndContainsKid() {
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

        String token = jwtService.generateAccessToken("user@test.com", 123L, "USER");

        Jws<Claims> jws = parse(token);

        assertEquals("ES256", jws.getHeader().getAlgorithm());
        assertEquals(KID, jws.getHeader().getKeyId());
    }

    @Test
    void accessTokenSignatureIsEs256Signature() {
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

        String token = jwtService.generateAccessToken("user@test.com", 123L, "USER");

        byte[] signature = Base64.getUrlDecoder().decode(token.split("\\.")[2]);

        assertEquals(64, signature.length);
    }

    @Test
    void accessTokenContainsExactlyContractClaims() {
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

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
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

        String token = jwtService.generateRefreshToken("user@test.com");

        Jws<Claims> jws = parse(token);

        assertEquals("ES256", jws.getHeader().getAlgorithm());
        assertEquals(KID, jws.getHeader().getKeyId());
    }

    @Test
    void refreshTokenContainsExactlyContractClaims() {
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

        String token = jwtService.generateRefreshToken("user@test.com");

        Claims claims = parse(token).getPayload();

        assertEquals(Set.of("sub", "jti", "iat", "exp"), claims.keySet());
        assertEquals("user@test.com", claims.getSubject());
        assertDoesNotThrow(() -> UUID.fromString(claims.getId()));
        assertLifetime(claims, REFRESH_EXPIRATION);
    }

    @Test
    void refreshTokensHaveUniqueJti() {
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

        String first = jwtService.generateRefreshToken("user@test.com");
        String second = jwtService.generateRefreshToken("user@test.com");

        assertNotEquals(jwtService.getJti(first), jwtService.getJti(second));
    }

    @Test
    void validRefreshTokenIsAccepted() {
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

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
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

        String token = signToken(generateKeyPair().getPrivate(), KID, REFRESH_EXPIRATION);

        assertThrows(SignatureException.class, () -> jwtService.getClaims(token));
    }

    @Test
    void tokenWithTamperedPayloadIsRejected() {
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

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
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, -1_000L);

        String token = jwtService.generateRefreshToken("user@test.com");

        assertThrows(ExpiredJwtException.class, () -> jwtService.getClaims(token));
    }

    @Test
    void tokenWithUnexpectedKidIsRejected() {
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

        String token = signToken(keyPair.getPrivate(), "unknown-key", REFRESH_EXPIRATION);

        assertThrows(JwtException.class, () -> jwtService.getClaims(token));
    }

    @Test
    void tokenWithoutKidIsRejected() {
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

        String token = signToken(keyPair.getPrivate(), null, REFRESH_EXPIRATION);

        assertThrows(JwtException.class, () -> jwtService.getClaims(token));
    }

    @Test
    void unsignedTokenIsRejected() {
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

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
        JwtService jwtService = createJwtService(keyPair, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

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
    void previouslyIssuedRefreshTokenRemainsValid() {
        JwtProperties properties = new JwtProperties();
        properties.setPrivateKey(toBase64Pem("PRIVATE KEY", keyPair.getPrivate().getEncoded()));
        properties.setPublicKey(GOLDEN_PUBLIC_KEY);
        properties.setKid(GOLDEN_KID);
        properties.setAccessExpiration(ACCESS_EXPIRATION);
        properties.setRefreshExpiration(REFRESH_EXPIRATION);

        JwtService jwtService = new JwtService(properties);
        ReflectionTestUtils.invokeMethod(jwtService, "initKeys");

        Claims claims = jwtService.getClaims(GOLDEN_REFRESH_TOKEN);

        assertEquals("golden@teamforge.test", claims.getSubject());
        assertEquals(GOLDEN_JTI, jwtService.getJti(GOLDEN_REFRESH_TOKEN));
        assertEquals(Instant.ofEpochSecond(4102444800L), jwtService.getExpiration(GOLDEN_REFRESH_TOKEN));
    }

    private JwtService createJwtService(KeyPair keyPair, long accessExpiration, long refreshExpiration) {
        JwtProperties properties = new JwtProperties();
        properties.setPrivateKey(toBase64Pem("PRIVATE KEY", keyPair.getPrivate().getEncoded()));
        properties.setPublicKey(toBase64Pem("PUBLIC KEY", keyPair.getPublic().getEncoded()));
        properties.setKid(KID);
        properties.setAccessExpiration(accessExpiration);
        properties.setRefreshExpiration(refreshExpiration);

        JwtService jwtService = new JwtService(properties);
        ReflectionTestUtils.invokeMethod(jwtService, "initKeys");

        return jwtService;
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token);
    }

    private String signToken(PrivateKey privateKey, String kid, long expirationMillis) {
        var builder = Jwts.builder()
                .subject("user@test.com")
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis));

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

    private static String toBase64Pem(String type, byte[] der) {
        String body = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.UTF_8)).encodeToString(der);
        String pem = "-----BEGIN " + type + "-----\n" + body + "\n-----END " + type + "-----\n";

        return Base64.getEncoder().encodeToString(pem.getBytes(StandardCharsets.UTF_8));
    }
}
