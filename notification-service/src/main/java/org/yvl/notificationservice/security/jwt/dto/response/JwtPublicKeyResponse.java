package org.yvl.notificationservice.security.jwt.dto.response;

import lombok.Data;

@Data
public class JwtPublicKeyResponse {
    private String kid;
    private String algorithm;
    private String publicKey;
}
