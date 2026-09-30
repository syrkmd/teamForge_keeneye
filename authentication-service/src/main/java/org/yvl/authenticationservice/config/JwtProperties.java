package org.yvl.authenticationservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String privateKey;
    private String publicKey;
    private String kid;
    private long accessExpiration;
    private long refreshExpiration;
}
