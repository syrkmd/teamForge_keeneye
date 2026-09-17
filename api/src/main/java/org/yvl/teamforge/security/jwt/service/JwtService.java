package org.yvl.teamforge.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yvl.teamforge.config.JwtProperties;
import org.yvl.teamforge.security.jwt.exception.JwtKeyInitializationException;
import org.yvl.teamforge.security.user.UserPrincipal;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public String generateAccessToken(UserPrincipal userPrincipal) {
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("userId", userPrincipal.getUser().getId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + properties.getAccessExpiration()))
                .header().keyId(properties.getKid()).and()
                .signWith(privateKey, Jwts.SIG.ES256)
                .compact();
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + properties.getRefreshExpiration()))
                .header().keyId(properties.getKid()).and()
                .signWith(privateKey, Jwts.SIG.ES256)
                .compact();
    }

    public String getJti(String token) {
        return getClaims(token).getId();
    }

    public Instant getExpiration(String token) {
        return getClaims(token).getExpiration().toInstant();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String encodedPrivateKey = properties.getPrivateKey();
        String pem = new String(Base64.getDecoder().decode(encodedPrivateKey), StandardCharsets.UTF_8);
        String key = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }

    private PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String encodedPublicKey = properties.getPublicKey();
        String pem = new String(Base64.getDecoder().decode(encodedPublicKey), StandardCharsets.UTF_8);
        String key = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePublic(keySpec);

    }

    @PostConstruct
    private void initKeys() {

        try {
            privateKey = getPrivateKey();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JwtKeyInitializationException(
                    "Failed to load private JWT key from configuration", e
            );
        }

        try {
            publicKey = getPublicKey();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JwtKeyInitializationException(
                    "Failed to load public JWT key from configuration", e
            );
        }
    }
}
