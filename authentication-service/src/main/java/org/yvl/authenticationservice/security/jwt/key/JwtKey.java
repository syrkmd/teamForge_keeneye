package org.yvl.authenticationservice.security.jwt.key;

import java.security.PrivateKey;
import java.security.PublicKey;

public record JwtKey(String kid, PublicKey publicKey, PrivateKey privateKey) {
    public JwtKey {
        JwtKeyValidator.validate(kid, publicKey, privateKey);
    }

    public String algorithm() { return JwtKeyValidator.ALGORITHM; }

    @Override
    public String toString() { return "JwtKey[kid=" + kid + ", algorithm=ES256]"; }
}
