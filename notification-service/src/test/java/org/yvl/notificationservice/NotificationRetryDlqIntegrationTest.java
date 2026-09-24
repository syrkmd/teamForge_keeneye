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
import org.yvl.notificationservice.consumer.event.NotificationEventPayload;
import org.yvl.notificationservice.repository.NotificationRepository;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Tag("integration")
@Testcontainers
@SpringBootTest(properties = {
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/notifications-schema.sql"
})
@ActiveProfiles("test")
class NotificationRetryDlqIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq:4-management");

    @Test
    void failedMessageIsMovedToDlqAfterRetries() {
        when(notificationRepository.findById(anyLong()))
                .thenThrow(new RuntimeException("Database is unavailable"));

        NotificationEventPayload payload = new NotificationEventPayload();
        payload.setNotificationId(1L);
        payload.setUserId(100L);

        rabbitTemplate.convertAndSend(
                "notification.events",
                "notification.invitation_received",
                payload
        );

        await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    var message = rabbitTemplate.receive(
                            "notification.service.queue.dlq"
                    );

                    assertNotNull(message);

                    verify(notificationRepository, timeout(5000).times(3))
                            .findById(1L);
                });
    }

}