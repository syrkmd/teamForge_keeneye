package org.yvl.notificationservice.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yvl.notificationservice.security.jwt.key.AuthJwtKeyProvider;

import java.security.PublicKey;

@Service
public class JwtService {
    private final String kid;
    private final JwtParser parser;

    public JwtService(AuthJwtKeyProvider keyProvider) {
        var key = keyProvider.load();

        this.kid = key.kid();

        this.parser = Jwts.parser()
                .verifyWith(key.publicKey())
                .sig()
                .clear()
                .add(Jwts.SIG.ES256)
                .and()
                .build();
    }

    public Jws<Claims> parseToken(String token) {
        Jws<Claims> jwt = parser.parseSignedClaims(token);

        if (!kid.equals(jwt.getHeader().getKeyId())) {
            throw new JwtException("Invalid JWT key id");
        }

        return jwt;
    }
}
