package org.yvl.teamforge;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.yvl.teamforge.config.JwtProperties;
import org.yvl.teamforge.entity.SystemRole;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.entity.enums.SystemRoleName;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JwtFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProperties jwtProperties;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17");

    @Test
    void adminTokenCanAccessAdminEndpoint() throws Exception {
        SystemRole role = SystemRole.builder()
                .name(SystemRoleName.ADMIN)
                .build();

        User user = User.builder()
                .id(123L)
                .email("admin@test.com")
                .systemRole(role)
                .isActive(true)
                .build();

        UserPrincipal userPrincipal = new UserPrincipal(user);

        String token = generateTestAccessToken(userPrincipal, 900_000);

        mockMvc.perform(
                        get("/admin/users")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());
    }

    @Test
    void userTokenCannotAccessAdminEndpoint() throws Exception {
        SystemRole role = SystemRole.builder()
                .name(SystemRoleName.USER)
                .build();

        User user = User.builder()
                .id(123L)
                .email("user@test.com")
                .systemRole(role)
                .isActive(true)
                .build();

        UserPrincipal userPrincipal = new UserPrincipal(user);

        String token = generateTestAccessToken(userPrincipal, 900_000);

        mockMvc.perform(
                        get("/admin/users")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void expiredTokenCannotAccessAdminEndpoint() throws Exception {
        SystemRole role = SystemRole.builder()
                .name(SystemRoleName.USER)
                .build();

        User user = User.builder()
                .id(123L)
                .email("user@test.com")
                .systemRole(role)
                .isActive(true)
                .build();

        UserPrincipal userPrincipal = new UserPrincipal(user);

        String token = generateTestAccessToken(userPrincipal, -1);

        mockMvc.perform(
                        get("/admin/users")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isUnauthorized());
    }

    private String generateTestAccessToken(UserPrincipal userPrincipal, long expirationMillis) throws Exception {
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("userId", userPrincipal.getUser().getId())
                .claim("role", userPrincipal.getUser().getSystemRole().getName().name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .header().keyId(jwtProperties.getKid()).and()
                .signWith(loadTestPrivateKey(), Jwts.SIG.ES256)
                .compact();
    }

    private PrivateKey loadTestPrivateKey() throws Exception {
        String encodedPrivateKey = System.getenv("JWT_PRIVATE_KEY");
        String pem = new String(Base64.getDecoder().decode(encodedPrivateKey), StandardCharsets.UTF_8);
        String key = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }
}
