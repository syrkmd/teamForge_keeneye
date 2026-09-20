package org.yvl.notificationservice.consumer;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.yvl.notificationservice.consumer.event.NotificationEventPayload;
import org.yvl.notificationservice.exception.NotificationNotFoundException;
import org.yvl.notificationservice.entity.Notification;
import org.yvl.notificationservice.repository.NotificationRepository;
import org.yvl.notificationservice.sse.SseConnectionManager;
import org.yvl.notificationservice.sse.mapper.NotificationMapper;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationRepository repository;
    private final SseConnectionManager sseConnectionManager;
    private final NotificationMapper mapper;

    @RabbitListener(
            queues = "${notification.notification-queue-name}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consumeNotificationEvent(
            NotificationEventPayload payload,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        Long notificationId = payload.getNotificationId();

        Notification notification = repository.findById(notificationId).orElseThrow(() ->
                new NotificationNotFoundException(notificationId));

        try {
            sseConnectionManager.push(notification.getUserId(), mapper.toNotificationView(notification));
        } catch (Exception e) {
            log.warn(
                    "Failed to push notification via SSE: notificationId={}, userId={}",
                    notificationId,
                    notification.getUserId(),
                    e
            );
        }

        channel.basicAck(deliveryTag, false);
    }
}
