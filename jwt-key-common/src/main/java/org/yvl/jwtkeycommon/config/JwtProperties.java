package org.yvl.jwtkeycommon.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Validated
public class JwtProperties {

    @NotNull
    private URI authUrl;
    @NotBlank
    private String keysEndpoint;
    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(2);
    @NotNull
    private Duration readTimeout = Duration.ofSeconds(3);

    @AssertTrue(message = "JWT Auth URL must be an absolute HTTP(S) base URL without credentials, query or fragment")
    public boolean isValidAuthUrl() {
        return authUrl != null && authUrl.getHost() != null
                && ("http".equals(authUrl.getScheme()) || "https".equals(authUrl.getScheme()))
                && authUrl.getUserInfo() == null && authUrl.getQuery() == null && authUrl.getFragment() == null
                && (authUrl.getPath().isEmpty() || "/".equals(authUrl.getPath()));
    }

    @AssertTrue(message = "JWT Auth timeouts must be positive")
    public boolean isValidTimeouts() {
        return connectTimeout != null && !connectTimeout.isNegative() && !connectTimeout.isZero()
                && readTimeout != null && !readTimeout.isNegative() && !readTimeout.isZero();
    }
}
