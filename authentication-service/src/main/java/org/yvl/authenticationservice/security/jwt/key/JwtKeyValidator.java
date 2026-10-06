package org.yvl.authenticationservice.security.jwt.key;

import org.yvl.authenticationservice.security.jwt.exception.JwtKeyInitializationException;

import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.util.regex.Pattern;

public class JwtKeyValidator {

    public static final String ALGORITHM = "ES256";

    private static final Pattern KID_PATTERN = Pattern.compile("[A-Za-z0-9._-]{1,64}");
    private static final byte[] PROBE = "jwt-key-pair-check".getBytes(StandardCharsets.UTF_8);
    private static final ECParameterSpec P256 = loadP256();

    private JwtKeyValidator() {
    }

    public static void validate(String kid, java.security.PublicKey publicKey, java.security.PrivateKey privateKey) {
        if (kid == null || !KID_PATTERN.matcher(kid).matches()) {
            throw new JwtKeyInitializationException("JWT key id has an invalid format");
        }
        if (!(publicKey instanceof ECPublicKey ecPublicKey) || !isP256(ecPublicKey)) {
            throw new JwtKeyInitializationException("JWT public key must be an EC P-256 key");
        }
        if (!(privateKey instanceof ECPrivateKey ecPrivateKey) || !isP256(ecPrivateKey)) {
            throw new JwtKeyInitializationException("JWT private key must be an EC P-256 key");
        }
        verifyKeyPair(ecPrivateKey, ecPublicKey);
    }

    private static void verifyKeyPair(ECPrivateKey privateKey, ECPublicKey publicKey) {
        try {
            Signature signer = Signature.getInstance("SHA256withECDSA");
            signer.initSign(privateKey);
            signer.update(PROBE);
            byte[] signature = signer.sign();

            Signature verifier = Signature.getInstance("SHA256withECDSA");
            verifier.initVerify(publicKey);
            verifier.update(PROBE);

            if (!verifier.verify(signature)) {
                throw new JwtKeyInitializationException("JWT private and public keys do not match");
            }
        } catch (GeneralSecurityException e) {
            throw new JwtKeyInitializationException("Failed to verify JWT key pair", e);
        }
    }

    private static boolean isP256(ECKey key) {
        ECParameterSpec params = key.getParams();

        return params != null
                && P256.getCurve().equals(params.getCurve())
                && P256.getGenerator().equals(params.getGenerator())
                && P256.getOrder().equals(params.getOrder())
                && P256.getCofactor() == params.getCofactor();
    }

    private static ECParameterSpec loadP256() {
        try {
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
            parameters.init(new ECGenParameterSpec("secp256r1"));

            return parameters.getParameterSpec(ECParameterSpec.class);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("EC P-256 is not available in this JVM", e);
        }
    }
}
