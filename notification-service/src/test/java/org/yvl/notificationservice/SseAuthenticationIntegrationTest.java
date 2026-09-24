package org.yvl.notificationservice;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.rabbitmq.RabbitMQContainer;
import org.yvl.notificationservice.entity.enums.NotificationType;
import org.yvl.notificationservice.sse.SseConnectionManager;
import org.yvl.notificationservice.sse.dto.NotificationView;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@Tag("integration")
@Testcontainers
@SpringBootTest(properties = {
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/notifications-schema.sql"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SseAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SseConnectionManager sseConnectionManager;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq:4-management");


    @Test
    void validTicketAllowsSseConnection() throws Exception {
        MvcResult ticketResult = mockMvc.perform(
                        post("/sse/ticket")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                100L,
                                                null,
                                                Collections.emptyList()
                                        )
                                ))
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = ticketResult.getResponse().getContentAsString();

        String ticket = objectMapper.readTree(response)
                .get("ticket")
                .asString();

        mockMvc.perform(
                get("/sse/stream")
                        .param("ticket", ticket)
        )
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void ticketCanBeUsedOnlyOnce() throws Exception {
        MvcResult ticketResult = mockMvc.perform(
                        post("/sse/ticket")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                100L,
                                                null,
                                                Collections.emptyList()
                                        )
                                ))
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = ticketResult.getResponse().getContentAsString();

        String ticket = objectMapper.readTree(response)
                .get("ticket")
                .asString();

        mockMvc.perform(
                        get("/sse/stream")
                                .param("ticket", ticket)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/sse/stream")
                                .param("ticket", ticket)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidTicketDoesNotAllowSseConnection() throws Exception {
        mockMvc.perform(
                        get("/sse/stream")
                                .param("ticket", "invalid-ticket")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void notificationIsSentToSseClient() throws Exception {
        MvcResult ticketResult = mockMvc.perform(
                        post("/sse/ticket")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                100L,
                                                null,
                                                Collections.emptyList()
                                        )
                                ))
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = ticketResult.getResponse().getContentAsString();

        String ticket = objectMapper.readTree(response)
                .get("ticket")
                .asString();

        MvcResult sseResult = mockMvc.perform(
                        get("/sse/stream")
                                .param("ticket", ticket)
                )
                .andExpect(status().isOk())
                .andReturn();

        NotificationView view = new NotificationView();
        view.setId(1L);
        view.setType(NotificationType.INVITATION_RECEIVED);
        view.setTitle("New invitation Test");
        view.setMessage("You have received an invitation");
        view.setCreatedAt(Instant.now());
        view.setRelatedInvitationId(10L);
        view.setRelatedTeamId(20L);

        sseConnectionManager.push(100L, view);

        String sseResponse = sseResult.getResponse().getContentAsString();

        assertTrue(sseResponse.contains("event:notification"));
        assertTrue(sseResponse.contains("\"id\":1"));
        assertTrue(sseResponse.contains("\"type\":\"INVITATION_RECEIVED\""));
        assertTrue(sseResponse.contains("\"message\":\"You have received an invitation\""));
    }
}