package org.yvl.notificationservice.security.jwt;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.yvl.jwtkeycommon.dto.response.LoadedKey;
import org.yvl.jwtkeycommon.key.AuthJwtKeyProvider;

import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@TestConfiguration(proxyBeanMethods = false)
public class TestJwtConfiguration {
    @Bean
    @Primary
    AuthJwtKeyProvider testJwtKeyProvider() throws GeneralSecurityException {
        var generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp256r1"));
        var provider = mock(AuthJwtKeyProvider.class);
        when(provider.load()).thenReturn(new LoadedKey(
                generator.generateKeyPair().getPublic(), "notification-integration-test"));
        return provider;
    }
}
