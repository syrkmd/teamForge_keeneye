package org.yvl.authenticationservice.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtException;
import org.yvl.authenticationservice.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;
import org.yvl.authenticationservice.security.jwt.key.JwtKey;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtKey key;
    private final long accessExpiration;
    private final long refreshExpiration;
    private final JwtParser parser;

    public JwtService(JwtKey key, JwtProperties properties) {
        this.key = key;
        this.accessExpiration = properties.getAccessExpiration();
        this.refreshExpiration = properties.getRefreshExpiration();
        this.parser = Jwts.parser().verifyWith(key.publicKey())
                .sig().clear().add(Jwts.SIG.ES256).and().build();
    }

    public String generateAccessToken(String email, Long userId, String role) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessExpiration))
                .header().keyId(key.kid()).and()
                .signWith(key.privateKey(), Jwts.SIG.ES256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(email)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(now))
                .expiration(new Date(now + refreshExpiration))
                .header().keyId(key.kid()).and()
                .signWith(key.privateKey(), Jwts.SIG.ES256)
                .compact();
    }

    public String getJti(String token) {
        return getClaims(token).getId();
    }

    public Instant getExpiration(String token) {
        return getClaims(token).getExpiration().toInstant();
    }

    public Claims getClaims(String token) {
        var jwt = parser.parseSignedClaims(token);
        if (!key.kid().equals(jwt.getHeader().getKeyId())) throw new JwtException("Invalid JWT key id");
        return jwt.getPayload();
    }
}
