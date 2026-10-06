package org.yvl.notificationservice.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yvl.notificationservice.security.jwt.dto.response.LoadedKey;
import org.yvl.notificationservice.security.jwt.key.AuthJwtKeyProvider;
import org.yvl.notificationservice.security.handler.JwtAuthenticationEntryPoint;
import org.yvl.notificationservice.security.jwt.filter.JwtFilter;
import org.yvl.notificationservice.security.jwt.service.JwtService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtContractTest {

    private static final String KID = "test-key-1";
    private static final long ACCESS_EXPIRATION = 900_000L;

    private static final String GOLDEN_KID = "teamforge-test-key";
    private static final String GOLDEN_PUBLIC_KEY =
            "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUZrd0V3WUhLb1pJemowQ0FRWUlLb1pJemowREFRY0RRZ0FFQTU0ZDlZa0k0a1dMMTlq" +
            "TDk4eGx4OWhsNnhkZgo2UmUxeXB3MHcyazlKS211aG9vZzI5aHpCbUJVMy84elhHaERDWFJtLzBkTXZudHNVWXlvV3pWWk9BPT0KLS0t" +
            "LS1FTkQgUFVCTElDIEtFWS0tLS0tCg==";
    private static final String GOLDEN_ACCESS_TOKEN =
            "eyJraWQiOiJ0ZWFtZm9yZ2UtdGVzdC1rZXkiLCJhbGciOiJFUzI1NiJ9." +
            "eyJzdWIiOiJnb2xkZW5AdGVhbWZvcmdlLnRlc3QiLCJ1c2VySWQiOjEyMywicm9sZSI6IlVTRVIiLCJpYXQiOjE3OTEwNDEyNTQsImV4cCI6" +
            "NDEwMjQ0NDgwMH0." +
            "6lghk3AFwQKpvBQAxKNTFVYMStMVizk3B6CnXiPbHYz0SJQ2YYcl-iK8qco7upVeZbs70tYji0AXrG3BAYNsCw";
    private static final String GOLDEN_REFRESH_TOKEN =
            "eyJraWQiOiJ0ZWFtZm9yZ2UtdGVzdC1rZXkiLCJhbGciOiJFUzI1NiJ9." +
            "eyJzdWIiOiJnb2xkZW5AdGVhbWZvcmdlLnRlc3QiLCJqdGkiOiI5ZTk2NTk5ZC02ZTRkLTRkNTItYmNkMC01MDAzMDk5NGU4ZDciLCJpYXQi" +
            "OjE3OTEwNDEyNTQsImV4cCI6NDEwMjQ0NDgwMH0." +
            "bnSZC-R3p9E7Ix217csVlmxquiCb9HDsZ2-X_dRM8lnQSNw2MOZKs9Ui9Xd8g0HUeSUh2hEjQ_xMuFUZV9lkdg";

    @Mock
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private final KeyPair keyPair = generateKeyPair();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validAccessTokenAuthenticatesUser() throws ServletException, IOException {
        String token = buildAccessToken(keyPair.getPrivate(), KID, ACCESS_EXPIRATION);

        createJwtFilter(toBase64Pem(keyPair.getPublic().getEncoded()), KID)
                .doFilter(requestWith(token), response, filterChain);

        assertAuthenticatedAsUser();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(authenticationEntryPoint);
    }

    @Test
    void tokenWithInvalidSignatureIsRejected() throws ServletException, IOException {
        String token = buildAccessToken(generateKeyPair().getPrivate(), KID, ACCESS_EXPIRATION);

        createJwtFilter(toBase64Pem(keyPair.getPublic().getEncoded()), KID)
                .doFilter(requestWith(token), response, filterChain);

        assertRejected(SignatureException.class);
    }

    @Test
    void tokenWithTamperedPayloadIsRejected() throws ServletException, IOException {
        String token = buildAccessToken(keyPair.getPrivate(), KID, ACCESS_EXPIRATION);

        String[] parts = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String tamperedPayload = payload.replace("\"userId\":123", "\"userId\":124");

        assertNotEquals(payload, tamperedPayload);

        String tamperedToken = parts[0] + "."
                + Base64.getUrlEncoder().withoutPadding().encodeToString(tamperedPayload.getBytes(StandardCharsets.UTF_8))
                + "." + parts[2];

        createJwtFilter(toBase64Pem(keyPair.getPublic().getEncoded()), KID)
                .doFilter(requestWith(tamperedToken), response, filterChain);

        assertRejected(SignatureException.class);
    }

    @Test
    void expiredTokenIsRejected() throws ServletException, IOException {
        String token = buildAccessToken(keyPair.getPrivate(), KID, -1_000L);

        createJwtFilter(toBase64Pem(keyPair.getPublic().getEncoded()), KID)
                .doFilter(requestWith(token), response, filterChain);

        assertRejected("JWT expired", ExpiredJwtException.class);
    }

    @Test
    void tokenWithUnexpectedKidIsRejected() throws ServletException, IOException {
        String token = buildAccessToken(keyPair.getPrivate(), "unknown-key", ACCESS_EXPIRATION);

        createJwtFilter(toBase64Pem(keyPair.getPublic().getEncoded()), KID)
                .doFilter(requestWith(token), response, filterChain);

        assertRejected(JwtException.class);
    }

    @Test
    void tokenWithoutKidIsRejected() throws ServletException, IOException {
        String token = buildAccessToken(keyPair.getPrivate(), null, ACCESS_EXPIRATION);

        createJwtFilter(toBase64Pem(keyPair.getPublic().getEncoded()), KID)
                .doFilter(requestWith(token), response, filterChain);

        assertRejected(JwtException.class);
    }

    @Test
    void unsignedTokenIsRejected() throws ServletException, IOException {
        String token = Jwts.builder()
                .subject("user@test.com")
                .claim("userId", 123L)
                .claim("role", "USER")
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .header().keyId(KID).and()
                .compact();

        createJwtFilter(toBase64Pem(keyPair.getPublic().getEncoded()), KID)
                .doFilter(requestWith(token), response, filterChain);

        assertRejected(JwtException.class);
    }

    @Test
    void tokenSignedWithHs256UsingPublicKeyAsSecretIsRejected() throws ServletException, IOException {
        String token = Jwts.builder()
                .subject("user@test.com")
                .claim("userId", 123L)
                .claim("role", "USER")
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .header().keyId(KID).and()
                .signWith(Keys.hmacShaKeyFor(keyPair.getPublic().getEncoded()), Jwts.SIG.HS256)
                .compact();

        createJwtFilter(toBase64Pem(keyPair.getPublic().getEncoded()), KID)
                .doFilter(requestWith(token), response, filterChain);

        assertRejected(JwtException.class);
    }

    @Test
    void refreshTokenIsNotAcceptedAsAccessToken() throws ServletException, IOException {
        String token = buildRefreshToken(keyPair.getPrivate(), KID);

        createJwtFilter(toBase64Pem(keyPair.getPublic().getEncoded()), KID)
                .doFilter(requestWith(token), response, filterChain);

        assertRejected(JwtException.class);
    }

    @Test
    void accessTokenIssuedByAuthenticationServiceIsAccepted() throws ServletException, IOException {
        createJwtFilter(GOLDEN_PUBLIC_KEY, GOLDEN_KID)
                .doFilter(requestWith(GOLDEN_ACCESS_TOKEN), response, filterChain);

        assertAuthenticatedAsUser();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(authenticationEntryPoint);
    }

    @Test
    void refreshTokenIssuedByAuthenticationServiceIsRejected() throws ServletException, IOException {
        createJwtFilter(GOLDEN_PUBLIC_KEY, GOLDEN_KID)
                .doFilter(requestWith(GOLDEN_REFRESH_TOKEN), response, filterChain);

        assertRejected(JwtException.class);
    }

    private HttpServletRequest requestWith(String token) {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        return request;
    }

    private void assertAuthenticatedAsUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(123L, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().isEmpty());
    }

    private void assertRejected(Class<? extends JwtException> cause) throws ServletException, IOException {
        assertRejected("JWT error", cause);
    }

    private void assertRejected(String message, Class<? extends JwtException> cause) throws ServletException, IOException {
        verify(authenticationEntryPoint).commence(
                eq(request),
                eq(response),
                argThat(exception -> message.equals(exception.getMessage()) && cause.isInstance(exception.getCause()))
        );
        verify(filterChain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private JwtFilter createJwtFilter(String encodedPublicKey, String kid) {
        try {
            String pem = new String(Base64.getDecoder().decode(encodedPublicKey), StandardCharsets.UTF_8);
            byte[] der = Base64.getDecoder().decode(pem.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", ""));
            var publicKey = java.security.KeyFactory.getInstance("EC").generatePublic(
                    new java.security.spec.X509EncodedKeySpec(der));
            var provider = mock(AuthJwtKeyProvider.class);
            when(provider.load()).thenReturn(new LoadedKey(publicKey, kid));
            JwtService jwtService = new JwtService(provider);
            return new JwtFilter(jwtService, authenticationEntryPoint);
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Invalid test key", e);
        }
    }

    private String buildAccessToken(PrivateKey privateKey, String kid, long expirationMillis) {
        var builder = Jwts.builder()
                .subject("user@test.com")
                .claim("userId", 123L)
                .claim("role", "USER")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis));

        if (kid != null) {
            builder.header().keyId(kid);
        }

        return builder
                .signWith(privateKey, Jwts.SIG.ES256)
                .compact();
    }

    private String buildRefreshToken(PrivateKey privateKey, String kid) {
        return Jwts.builder()
                .subject("user@test.com")
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 604_800_000L))
                .header().keyId(kid).and()
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

    private static String toBase64Pem(byte[] der) {
        String body = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.UTF_8)).encodeToString(der);
        String pem = "-----BEGIN " + "PUBLIC KEY" + "-----\n" + body + "\n-----END " + "PUBLIC KEY" + "-----\n";

        return Base64.getEncoder().encodeToString(pem.getBytes(StandardCharsets.UTF_8));
    }
}
