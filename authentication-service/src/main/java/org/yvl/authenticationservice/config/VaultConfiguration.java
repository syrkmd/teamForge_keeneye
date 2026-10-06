package org.yvl.authenticationservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.*;
import org.springframework.vault.client.VaultClients;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.yvl.authenticationservice.security.jwt.key.VaultJwtKeyProvider;
import org.yvl.authenticationservice.security.jwt.key.JwtKey;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Map;

@Configuration()
@EnableConfigurationProperties(VaultProperties.class)
public class VaultConfiguration {

    @Bean
    public JdkClientHttpRequestFactory vaultRequestFactory(VaultProperties properties) {
        if (properties.uri().getScheme().equals("http")
                && !properties.allowLocalHttp()) {
            throw new VaultException("HTTP Vault endpoint is not permitted");
        }
        var client = HttpClient.newBuilder().connectTimeout(properties.connectTimeout())
                .followRedirects(HttpClient.Redirect.NEVER).build();
        var factory = new JdkClientHttpRequestFactory(client);
        factory.setReadTimeout(properties.readTimeout());
        return factory;
    }

    @Bean
    public SessionManager vaultSessionManager(
            VaultProperties properties,
            JdkClientHttpRequestFactory vaultRequestFactory
    ) {
        var endpoint = VaultEndpoint.from(properties.uri());
        var client = VaultClients.createRestTemplate(endpoint, vaultRequestFactory);

        var options = AppRoleAuthenticationOptions.builder()
                .path(properties.authMount())
                .roleId(AppRoleAuthenticationOptions.RoleId.provided(properties.roleId()))
                .secretId(AppRoleAuthenticationOptions.SecretId.provided(properties.secretId()))
                .build();

        var authentication = new AppRoleAuthentication(options, client);

        return new SimpleSessionManager(authentication);
    }

    @Bean
    public VaultTemplate jwtVaultTemplate(
            VaultProperties properties,
            JdkClientHttpRequestFactory vaultRequestFactory,
            SessionManager vaultSessionManager
    ) {
        return new VaultTemplate(VaultEndpoint.from(properties.uri()), vaultRequestFactory, vaultSessionManager);
    }

    @Bean
    public VaultVersionedKeyValueOperations jwtVaultOperations(
            VaultTemplate jwtVaultTemplate,
            VaultProperties properties
    ) {
        return jwtVaultTemplate.opsForVersionedKeyValue(properties.mount());
    }

    @Bean
    public VaultJwtKeyProvider vaultJwtKeyProvider(
            VaultVersionedKeyValueOperations operations,
            VaultProperties properties
    ) {
        return new VaultJwtKeyProvider(operations, properties);
    }

    @Bean
    public JwtKey jwtKey(VaultJwtKeyProvider provider) {
        return provider.load();
    }
}

