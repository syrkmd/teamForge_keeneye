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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.yvl.jwtkeycommon.dto.response.LoadedKey;
import org.yvl.jwtkeycommon.key.AuthJwtKeyProvider;
import org.yvl.teamforge.entity.SystemRole;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.entity.enums.SystemRoleName;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.security.KeyPair;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@Testcontainers
@SpringBootTest
@Import(JwtFilterIntegrationTest.JwtTestConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JwtFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_KID = "api-integration-test";
    private static final KeyPair TEST_KEY_PAIR = Jwts.SIG.ES256.keyPair().build();

    @TestConfiguration(proxyBeanMethods = false)
    static class JwtTestConfiguration {
        @Bean
        @Primary
        AuthJwtKeyProvider testJwtKeyProvider() {
            var provider = mock(AuthJwtKeyProvider.class);
            when(provider.load()).thenReturn(new LoadedKey(TEST_KEY_PAIR.getPublic(), TEST_KID));
            return provider;
        }
    }

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
                .header().keyId(TEST_KID).and()
                .signWith(TEST_KEY_PAIR.getPrivate(), Jwts.SIG.ES256)
                .compact();
    }

}
