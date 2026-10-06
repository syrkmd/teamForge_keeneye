package org.yvl.authenticationservice.config;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;

@Validated
@ConfigurationProperties("jwt.vault")
public record VaultProperties(
        @NotNull URI uri,
        @NotBlank String roleId,
        @NotBlank String secretId,
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]+") String mount,
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]+(/[A-Za-z0-9_-]+)*") String secretPath,
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]+") String authMount,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout,
        boolean allowLocalHttp
) {

    @AssertTrue(message = "Vault URI must be an absolute HTTP(S) endpoint without credentials, query or fragment")
    public boolean isValidEndpoint() {
        return uri != null && uri.getHost() != null && uri.getUserInfo() == null
                && uri.getQuery() == null && uri.getFragment() == null
                && (uri.getPath().isEmpty() || uri.getPath().equals("/"))
                && (uri.getScheme().equals("https") || uri.getScheme().equals("http"));
    }

    @AssertTrue(message = "Vault timeouts must be positive and bounded")
    public boolean isValidTiming() {
        return positive(connectTimeout) && positive(readTimeout)
                && connectTimeout.compareTo(Duration.ofSeconds(30)) <= 0
                && readTimeout.compareTo(Duration.ofSeconds(30)) <= 0;
    }

    private static boolean positive(Duration value) {
        return value != null && !value.isNegative() && !value.isZero();
    }
}
