package org.yvl.jwtkeycommon.key;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.yvl.jwtkeycommon.config.JwtProperties;
import org.yvl.jwtkeycommon.dto.response.JwtPublicKeyResponse;
import org.yvl.jwtkeycommon.exception.JwtKeyInitializationException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AuthJwtKeyProviderTest {
    private static final String ENDPOINT = "/internal/jwt/keys";
    private static final String KID = "test-public-key_1";
    private static final String ERROR = "Failed to load public JWT key from Authentication Service";
    private static final PublicKey PUBLIC_KEY = ecKey("secp256r1");
    private static final String PEM = pem(PUBLIC_KEY);

    private final AtomicInteger requests = new AtomicInteger();
    private final ObjectMapper mapper = new ObjectMapper();
    private HttpServer server;
    private JwtProperties properties;
    private String body;
    private int status;
    private long responseDelayMillis;

    @BeforeEach
    void startServer() throws IOException {
        status = 200;
        body = mapper.writeValueAsString(response(KID, "ES256", PEM));
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(ENDPOINT, exchange -> {
            requests.incrementAndGet();
            try (exchange) {
                assertEquals("GET", exchange.getRequestMethod());
                if (responseDelayMillis > 0) {
                    try {
                        Thread.sleep(responseDelayMillis);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                if (status == 302) exchange.getResponseHeaders().set("Location", "/redirected");
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(status, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        });
        server.start();
        properties = new JwtProperties();
        properties.setAuthUrl(URI.create("http://127.0.0.1:" + server.getAddress().getPort()));
        properties.setKeysEndpoint(ENDPOINT);
        properties.setConnectTimeout(Duration.ofSeconds(1));
        properties.setReadTimeout(Duration.ofSeconds(1));
    }

    @AfterEach
    void stopServer() {
        if (server != null) server.stop(0);
    }

    @Test
    void loadsP256PublicKeyWithOneGet() {
        var loaded = new AuthJwtKeyProvider(properties).load();
        assertEquals(KID, loaded.kid());
        assertArrayEquals(PUBLIC_KEY.getEncoded(), loaded.publicKey().getEncoded());
        assertEquals(1, requests.get());
    }

    @ParameterizedTest
    @MethodSource("invalidResponses")
    void rejectsInvalidDocumentWithoutRetryOrSensitiveCause(JwtPublicKeyResponse response) {
        body = mapper.writeValueAsString(response);
        assertSafeFailure();
        assertEquals(1, requests.get());
    }

    static Stream<JwtPublicKeyResponse> invalidResponses() {
        return Stream.of(
                response(null, "ES256", PEM),
                response("", "ES256", PEM),
                response("invalid kid", "ES256", PEM),
                response("x".repeat(65), "ES256", PEM),
                response(KID, null, PEM),
                response(KID, "HS256", PEM),
                response(KID, "ES384", PEM),
                response(KID, "ES256", null),
                response(KID, "ES256", ""),
                response(KID, "ES256", "not a PEM"),
                response(KID, "ES256", "-----BEGIN PUBLIC KEY-----\n-----END PUBLIC KEY-----"),
                response(KID, "ES256", "-----BEGIN PUBLIC KEY-----\nnot-base64!\n-----END PUBLIC KEY-----"),
                response(KID, "ES256", "-----BEGIN PUBLIC KEY-----\nYWJj\n-----END PUBLIC KEY-----"),
                response(KID, "ES256", PEM.replace("END PUBLIC KEY", "END CERTIFICATE")),
                response(KID, "ES256", pem(ecKey("secp384r1"))),
                response(KID, "ES256", pem(rsaKey()))
        );
    }

    @Test
    void rejectsMissingDocument() {
        body = "null";
        assertSafeFailure();
        assertEquals(1, requests.get());
    }

    @Test
    void rejectsMalformedJsonWithoutLeakingBody() {
        body = "sensitive-upstream-marker";
        assertSafeFailure();
        assertEquals(1, requests.get());
    }

    @Test
    void rejectsHttpErrorWithoutLeakingBodyOrRetrying() {
        status = 503;
        body = "sensitive-upstream-marker";
        assertSafeFailure();
        assertEquals(1, requests.get());
    }

    @Test
    void doesNotFollowRedirects() {
        var redirectedRequests = new AtomicInteger();
        server.createContext("/redirected", exchange -> {
            redirectedRequests.incrementAndGet();
            exchange.close();
        });
        status = 302;
        body = "";
        assertSafeFailure();
        assertEquals(1, requests.get());
        assertEquals(0, redirectedRequests.get());
    }

    @Test
    void failsWhenAuthenticationServiceIsUnavailable() {
        server.stop(0);
        server = null;
        assertSafeFailure();
        assertEquals(0, requests.get());
    }

    @Test
    void failsOnReadTimeoutWithoutRetry() {
        properties.setReadTimeout(Duration.ofMillis(100));
        responseDelayMillis = 500;
        assertTimeoutPreemptively(Duration.ofSeconds(3), this::assertSafeFailure);
        assertEquals(1, requests.get());
    }

    private void assertSafeFailure() {
        var error = assertThrows(JwtKeyInitializationException.class,
                () -> new AuthJwtKeyProvider(properties).load());
        assertEquals(ERROR, error.getMessage());
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
    }

    private static JwtPublicKeyResponse response(String kid, String algorithm, String publicKey) {
        var response = new JwtPublicKeyResponse();
        response.setKid(kid);
        response.setAlgorithm(algorithm);
        response.setPublicKey(publicKey);
        return response;
    }

    private static String pem(PublicKey key) {
        return "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(key.getEncoded())
                + "\n-----END PUBLIC KEY-----";
    }

    private static PublicKey ecKey(String curve) {
        try {
            var generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(new ECGenParameterSpec(curve));
            return generator.generateKeyPair().getPublic();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Cannot create test public key", e);
        }
    }

    private static PublicKey rsaKey() {
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair().getPublic();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Cannot create test public key", e);
        }
    }
}
