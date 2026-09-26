package org.yvl.teamforge;

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
import org.yvl.teamforge.security.jwt.service.JwtService;
import org.yvl.teamforge.security.user.UserPrincipal;

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
    private JwtService jwtService;

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

        String token = jwtService.generateAccessToken(userPrincipal);

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

        String token = jwtService.generateAccessToken(userPrincipal);

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

        long originalExpiration = jwtProperties.getAccessExpiration();

        try {
            jwtProperties.setAccessExpiration(-1);

            String token = jwtService.generateAccessToken(userPrincipal);

            mockMvc.perform(
                            get("/admin/users")
                                    .header("Authorization", "Bearer " + token)
                    )
                    .andExpect(status().isUnauthorized());
        } finally {
            jwtProperties.setAccessExpiration(originalExpiration);
        }
    }
}
