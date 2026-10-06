package org.yvl.authenticationservice.security.jwt.key;

import org.yvl.authenticationservice.config.VaultProperties;
import org.yvl.authenticationservice.security.jwt.exception.JwtKeyInitializationException;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.support.Versioned;
import java.util.Map;
import java.util.Set;

public class VaultJwtKeyProvider {
    private final VaultVersionedKeyValueOperations operations;
    private final VaultProperties properties;

    public VaultJwtKeyProvider(VaultVersionedKeyValueOperations operations, VaultProperties properties) {
        this.operations = operations;
        this.properties = properties;
    }

    public JwtKey load() {
        Versioned<Map<String, Object>> result;
        try {
            result = operations.get(properties.secretPath());
        } catch (RuntimeException ignored) {
            throw new JwtKeyInitializationException("JWT Vault initialization failed");
        }
        if (result == null || !result.hasData() || !result.hasMetadata()
                || result.getRequiredMetadata().isDeleted() || result.getRequiredMetadata().isDestroyed()) {
            throw new JwtKeyInitializationException("JWT secret is unavailable");
        }
        return parse(result.getRequiredData());
    }

    static JwtKey parse(Map<String, Object> document) {
        try {
            if (document == null || !document.keySet().equals(Set.of("kid", "privateKey", "publicKey"))) {
                throw new IllegalArgumentException();
            }
            return new JwtKey(text(document.get("kid")),
                    JwtKeyPemParser.parsePublicKey(text(document.get("publicKey"))),
                    JwtKeyPemParser.parsePrivateKey(text(document.get("privateKey"))));
        } catch (RuntimeException ignored) {
            throw new JwtKeyInitializationException("Invalid JWT Vault document");
        }
    }

    private static String text(Object value) {
        if (value instanceof String text && !text.isBlank()) return text;
        throw new IllegalArgumentException();
    }
}
