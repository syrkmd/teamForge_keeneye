package org.yvl.authenticationservice.security.jwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtPublicKeyResponse {
    private String kid;
    private String algorithm;
    private String publicKey;
}
