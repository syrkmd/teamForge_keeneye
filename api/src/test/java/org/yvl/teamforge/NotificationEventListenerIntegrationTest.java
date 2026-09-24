package org.yvl.teamforge;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.yvl.teamforge.entity.enums.NotificationType;
import org.yvl.teamforge.notification.event.NotificationRequestedEvent;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Tag("integration")
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class NotificationEventListenerIntegrationTest {

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17");

    @Test
    void notificationIsPublishedAfterCommit() {
        NotificationRequestedEvent event =
                new NotificationRequestedEvent(
                        1L,
                        1L,
                        NotificationType.INVITATION_RECEIVED
                );

        transactionTemplate.execute(_ -> {
            eventPublisher.publishEvent(event);
            return null;
        });

        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() ->
                        verify(rabbitTemplate).convertAndSend(
                                anyString(),
                                anyString(),
                                any(Object.class)
                        )
                );
    }

    @Test
    void notificationIsNotPublishedAfterRollback() {
        NotificationRequestedEvent event =
                new NotificationRequestedEvent(
                        1L,
                        1L,
                        NotificationType.INVITATION_RECEIVED
                );

        transactionTemplate.execute(status -> {
            eventPublisher.publishEvent(event);
            status.setRollbackOnly();
            return null;
        });

        await()
                .during(Duration.ofSeconds(2))
                .untilAsserted(() ->
                        verify(rabbitTemplate, never()).convertAndSend(
                                anyString(),
                                anyString(),
                                any(Object.class)
                        )
                );
    }
}
