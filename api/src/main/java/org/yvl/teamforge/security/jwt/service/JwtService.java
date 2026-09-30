package org.yvl.teamforge.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yvl.teamforge.config.JwtProperties;
import org.yvl.teamforge.security.jwt.exception.JwtKeyInitializationException;

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

    public Claims getClaims(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token);

        String kid = jws.getHeader().getKeyId();

        if (!properties.getKid().equals(kid)) {
            throw new JwtException("Invalid JWT key id");
        }

        return jws.getPayload();
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
            publicKey = getPublicKey();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JwtKeyInitializationException(
                    "Failed to load public JWT key from configuration", e
            );
        }
    }
}
