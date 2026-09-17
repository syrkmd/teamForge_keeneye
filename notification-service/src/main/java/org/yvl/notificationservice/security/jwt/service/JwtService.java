package org.yvl.notificationservice.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yvl.notificationservice.config.JwtProperties;
import org.yvl.notificationservice.security.jwt.exception.JwtKeyInitializationException;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;
    private PublicKey publicKey;

    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token);
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
    private void initKey() {
        try {
            publicKey = getPublicKey();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JwtKeyInitializationException(
                    "Failed to load public JWT key from configuration", e
            );
        }
    }
}
