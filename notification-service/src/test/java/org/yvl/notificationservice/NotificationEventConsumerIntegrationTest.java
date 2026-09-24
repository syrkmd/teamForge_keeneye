package org.yvl.notificationservice;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.rabbitmq.RabbitMQContainer;
import org.yvl.notificationservice.config.RabbitMQConfig;
import org.yvl.notificationservice.consumer.event.NotificationEventPayload;
import org.yvl.notificationservice.entity.Notification;
import org.yvl.notificationservice.entity.enums.NotificationType;
import org.yvl.notificationservice.repository.NotificationRepository;
import org.yvl.notificationservice.sse.SseConnectionManager;
import org.yvl.notificationservice.sse.dto.NotificationView;

import java.time.Duration;
import java.time.Instant;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

@Tag("integration")
@Testcontainers
@SpringBootTest(properties = {
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/notifications-schema.sql"
})
@ActiveProfiles("test")
public class NotificationEventConsumerIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @MockitoBean
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
    void notificationEventIsConsumedAndPushedToSse() {
        Notification notification = Notification.builder()
                .type(NotificationType.INVITATION_RECEIVED)
                .title("Test Invitation Received")
                .message("Test message")
                .isRead(false)
                .createdAt(Instant.now())
                .userId(100L)
                .relatedInvitationId(200L)
                .relatedTeamId(300L)
                .build();

        notificationRepository.saveAndFlush(notification);

        NotificationEventPayload payload = new NotificationEventPayload();
        payload.setNotificationId(notification.getId());
        payload.setUserId(notification.getUserId());

        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getNotificationExchangeName(),
                "notification.invitation_received",
                payload
        );

        NotificationView expected = new NotificationView();
        expected.setId(notification.getId());
        expected.setType(NotificationType.INVITATION_RECEIVED);
        expected.setTitle("Test Invitation Received");
        expected.setMessage("Test message");
        expected.setCreatedAt(notification.getCreatedAt());
        expected.setRelatedInvitationId(200L);
        expected.setRelatedTeamId(300L);

        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() ->
                        verify(sseConnectionManager).push(
                                notification.getUserId(),
                                expected
                        )
                );

        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() ->
                        assertNull(
                                rabbitTemplate.receive(
                                        rabbitMQConfig.getNotificationQueueName()
                                )
                        )
                );
    }
    
}
