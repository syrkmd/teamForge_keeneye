package org.yvl.teamforge.security.hash;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    public String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(value.getBytes());

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }
}
