package org.yvl.notificationservice.consumer;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yvl.notificationservice.consumer.event.NotificationEventPayload;
import org.yvl.notificationservice.entity.Notification;
import org.yvl.notificationservice.entity.enums.NotificationType;
import org.yvl.notificationservice.exception.NotificationNotFoundException;
import org.yvl.notificationservice.repository.NotificationRepository;
import org.yvl.notificationservice.sse.SseConnectionManager;
import org.yvl.notificationservice.sse.dto.NotificationView;
import org.yvl.notificationservice.sse.mapper.NotificationMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEventConsumerTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private SseConnectionManager sseConnectionManager;

    @Mock
    private NotificationMapper mapper;

    @Mock
    private Channel channel;

    @InjectMocks
    private NotificationEventConsumer consumer;

    @Test
    void notificationIsAckedWhenSsePushFails() throws IOException {
        Notification notification = Notification.builder()
                .id(1L)
                .type(NotificationType.INVITATION_RECEIVED)
                .title("New invitation")
                .message("You have received a team invitation")
                .isRead(false)
                .createdAt(Instant.now())
                .userId(100L)
                .relatedInvitationId(200L)
                .relatedTeamId(300L)
                .build();

        when(repository.findById(notification.getId()))
                .thenReturn(Optional.of(notification));

        NotificationView notificationView = new NotificationView();

        when(mapper.toNotificationView(notification))
                .thenReturn(notificationView);

        doThrow(new RuntimeException("SSE failed"))
                .when(sseConnectionManager)
                .push(anyLong(), any());

        NotificationEventPayload payload = new NotificationEventPayload();
        payload.setNotificationId(notification.getId());
        payload.setUserId(notification.getUserId());

        long deliveryTag = 1L;

        consumer.consumeNotificationEvent(
                payload,
                channel,
                deliveryTag
        );

        verify(sseConnectionManager).push(
                notification.getUserId(),
                notificationView
        );

        verify(channel).basicAck(deliveryTag, false);
    }

    @Test
    void notificationIsNotFoundWhenNotificationDoesNotExist() throws IOException {
        Long notificationId = 999L;

        when(repository.findById(notificationId))
                .thenReturn(Optional.empty());

        NotificationEventPayload payload = new NotificationEventPayload();
        payload.setNotificationId(notificationId);
        payload.setUserId(100L);

        assertThrows(
                NotificationNotFoundException.class,
                () -> consumer.consumeNotificationEvent(
                        payload,
                        channel,
                        1L
                )
        );

        verify(sseConnectionManager, never()).push(anyLong(), any());
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }
}