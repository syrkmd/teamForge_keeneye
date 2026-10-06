package org.yvl.authenticationservice.security.jwt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yvl.authenticationservice.security.jwt.dto.response.JwtPublicKeyResponse;
import org.yvl.authenticationservice.security.jwt.key.JwtKey;

import java.security.PublicKey;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
public class InternalJwtKeyController {

    private final JwtKey jwtKey;

    @GetMapping("/internal/jwt/keys")
    public JwtPublicKeyResponse getKeys() {
        return new JwtPublicKeyResponse(jwtKey.kid(), jwtKey.algorithm(), toPem(jwtKey.publicKey()));
    }

    private static String toPem(PublicKey publicKey) {
        String encoded = Base64.getMimeEncoder(64, new byte[]{'\n'})
                .encodeToString(publicKey.getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" + encoded + "\n-----END PUBLIC KEY-----\n";
    }
}
