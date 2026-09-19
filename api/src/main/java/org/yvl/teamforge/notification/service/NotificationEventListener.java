package org.yvl.teamforge.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.yvl.teamforge.config.RabbitMQConfig;
import org.yvl.teamforge.notification.event.NotificationEventPayload;
import org.yvl.teamforge.notification.event.NotificationRequestedEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotification(NotificationRequestedEvent event) {
        String routingKey = "notification." + event.getType().name().toLowerCase();
        NotificationEventPayload payload = new NotificationEventPayload();

        payload.setNotificationId(event.getNotificationId());
        payload.setUserId(event.getUserId());

        try {
            rabbitTemplate.convertAndSend(rabbitMQConfig.getNotificationExchangeName(), routingKey, payload);
            log.info(
                    "Notification event published: notificationId={}, userId={}, routingKey={}",
                    event.getNotificationId(),
                    event.getUserId(),
                    routingKey
            );
        } catch (Exception e) {
            log.error(
                    "Failed to publish notification event: notificationId={}, userId={}, routingKey={}",
                    event.getNotificationId(),
                    event.getUserId(),
                    routingKey,
                    e
            );
        }
    }
}
