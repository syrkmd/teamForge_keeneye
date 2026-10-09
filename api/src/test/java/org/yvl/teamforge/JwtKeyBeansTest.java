package org.yvl.teamforge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.MapPropertySource;
import org.yvl.jwtkeycommon.config.JwtProperties;
import org.yvl.jwtkeycommon.key.AuthJwtKeyProvider;
import org.yvl.teamforge.security.jwt.service.JwtService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtKeyBeansTest {
    @Test
    void applicationExplicitlyRegistersOneProviderAndPropertiesBean() {
        try (var context = newContext()) {
            context.refresh();
            assertEquals(1, context.getBeansOfType(AuthJwtKeyProvider.class).size());
            assertEquals(1, context.getBeansOfType(JwtProperties.class).size());
            assertEquals("/internal/jwt/keys", context.getBean(JwtProperties.class).getKeysEndpoint());
        }
    }

    @Test
    void existingTestProviderSuppliesKeyToJwtServiceWithoutHttp() {
        try (var context = newContext()) {
            context.register(JwtFilterIntegrationTest.JwtTestConfiguration.class, JwtService.class);
            context.refresh();
            var provider = context.getBean(AuthJwtKeyProvider.class);
            assertTrue(mockingDetails(provider).isMock());
            assertNotNull(context.getBean(JwtService.class));
            verify(provider, times(1)).load();
            assertEquals(1, context.getBeansOfType(JwtProperties.class).size());
        }
    }

    private AnnotationConfigApplicationContext newContext() {
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("jwt-test", Map.of(
                "jwt.auth-url", "http://127.0.0.1:1",
                "jwt.keys-endpoint", "/internal/jwt/keys")));
        ConfigurationPropertiesBindingPostProcessor.register(context);
        context.register(TeamForgeApplication.class.getAnnotation(Import.class).value());
        return context;
    }
}
