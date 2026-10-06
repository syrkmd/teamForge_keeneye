package org.yvl.notificationservice.security.jwt.key;

import lombok.RequiredArgsConstructor;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.yvl.notificationservice.config.JwtProperties;
import org.yvl.notificationservice.security.jwt.dto.response.JwtPublicKeyResponse;
import org.yvl.notificationservice.security.jwt.dto.response.LoadedKey;
import org.yvl.notificationservice.security.jwt.exception.JwtKeyInitializationException;

import java.net.http.HttpClient;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AuthJwtKeyProvider {
    private final JwtProperties properties;

    public LoadedKey load() {
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(properties.getConnectTimeout())
                .followRedirects(HttpClient.Redirect.NEVER)
                .build()) {
            var factory = new JdkClientHttpRequestFactory(client);
            factory.setReadTimeout(properties.getReadTimeout());
            JwtPublicKeyResponse response = RestClient.builder().requestFactory(factory).build()
                    .get().uri(properties.getAuthUrl().resolve(properties.getKeysEndpoint()))
                    .retrieve().body(JwtPublicKeyResponse.class);
            return parse(response);
        } catch (RuntimeException | GeneralSecurityException ignored) {
            throw new JwtKeyInitializationException("Failed to load public JWT key from Authentication Service", null);
        }
    }

    private LoadedKey parse(JwtPublicKeyResponse response) throws GeneralSecurityException {
        if (response == null || response.getKid() == null || !response.getKid().matches("[A-Za-z0-9._-]{1,64}")
                || !"ES256".equals(response.getAlgorithm()) || response.getPublicKey() == null) {
            throw new IllegalArgumentException();
        }
        String pem = response.getPublicKey().strip();
        String begin = "-----BEGIN PUBLIC KEY-----";
        String end = "-----END PUBLIC KEY-----";
        if (!pem.startsWith(begin) || !pem.endsWith(end)) throw new IllegalArgumentException();
        String encoded = pem.substring(begin.length(), pem.length() - end.length()).replaceAll("\\s", "");
        if (encoded.isEmpty()) throw new IllegalArgumentException();
        PublicKey key = KeyFactory.getInstance("EC").generatePublic(
                new X509EncodedKeySpec(Base64.getDecoder().decode(encoded)));
        if (!(key instanceof ECPublicKey ecKey)) throw new IllegalArgumentException();

        AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
        parameters.init(new ECGenParameterSpec("secp256r1"));
        ECParameterSpec expected = parameters.getParameterSpec(ECParameterSpec.class);
        ECParameterSpec actual = ecKey.getParams();
        if (actual == null || !expected.getCurve().equals(actual.getCurve())
                || !expected.getGenerator().equals(actual.getGenerator())
                || !expected.getOrder().equals(actual.getOrder())
                || expected.getCofactor() != actual.getCofactor()) {
            throw new IllegalArgumentException();
        }
        return new LoadedKey(key, response.getKid());
    }
}
