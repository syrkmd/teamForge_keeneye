package org.yvl.jwtkeycommon.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.yvl.jwtkeycommon.key.AuthJwtKeyProvider;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class JwtPropertiesTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(Registration.class)
            .withPropertyValues("jwt.auth-url=http://127.0.0.1:1",
                    "jwt.keys-endpoint=/internal/jwt/keys");

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties
    @Import({JwtProperties.class, AuthJwtKeyProvider.class})
    static class Registration {}

    @Test
    void bindsPropertiesAndRegistersOneBeanOfEachTypeWithoutHttp() {
        runner.run(context -> {
            assertThat(context).hasNotFailed()
                    .hasSingleBean(JwtProperties.class)
                    .hasSingleBean(AuthJwtKeyProvider.class);
            var properties = context.getBean(JwtProperties.class);
            assertThat(properties.getKeysEndpoint()).isEqualTo("/internal/jwt/keys");
            assertThat(properties.getConnectTimeout()).isEqualTo(Duration.ofSeconds(2));
            assertThat(properties.getReadTimeout()).isEqualTo(Duration.ofSeconds(3));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "jwt.auth-url=/relative",
            "jwt.auth-url=http://user:password@localhost",
            "jwt.auth-url=http://localhost/path",
            "jwt.keys-endpoint=",
            "jwt.connect-timeout=0s",
            "jwt.read-timeout=-1s"
    })
    void rejectsInvalidProperties(String property) {
        runner.withPropertyValues(property).run(context -> assertThat(context).hasFailed());
    }
}
