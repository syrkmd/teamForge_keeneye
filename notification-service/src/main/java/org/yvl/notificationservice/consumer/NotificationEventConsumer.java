package org.yvl.notificationservice.consumer;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.yvl.notificationservice.consumer.event.NotificationEventPayload;
import org.yvl.notificationservice.consumer.exception.NotificationNotFoundException;
import org.yvl.notificationservice.repository.NotificationRepository;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationRepository repository;

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

        repository.findById(notificationId).orElseThrow(() -> new NotificationNotFoundException(notificationId));

        channel.basicAck(deliveryTag, false);
    }
}
