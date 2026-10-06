package org.yvl.authenticationservice.security.jwt.key;

import org.yvl.authenticationservice.security.jwt.exception.JwtKeyInitializationException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class JwtKeyPemParser {

    private JwtKeyPemParser() {
    }

    public static PrivateKey parsePrivateKey(String pem) {
        if (pem == null) {
            throw new JwtKeyInitializationException("Private JWT key is not provided");
        }

        byte[] keyBytes = decodeBody(pem, "PRIVATE KEY", "private");

        try {
            return KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JwtKeyInitializationException("Failed to load private JWT key from configuration", e);
        }
    }

    public static PublicKey parsePublicKey(String pem) {
        if (pem == null) {
            throw new JwtKeyInitializationException("Public JWT key is not provided");
        }

        byte[] keyBytes = decodeBody(pem, "PUBLIC KEY", "public");

        try {
            return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JwtKeyInitializationException("Failed to load public JWT key from configuration", e);
        }
    }

    private static byte[] decodeBody(String pem, String type, String name) {
        String body = pem
                .replace("-----BEGIN " + type + "-----", "")
                .replace("-----END " + type + "-----", "")
                .replaceAll("\\s", "");

        try {
            return Base64.getDecoder().decode(body);
        } catch (IllegalArgumentException e) {
            throw new JwtKeyInitializationException("Failed to load " + name + " JWT key from configuration: invalid Base64 content");
        }
    }
}
