package org.yvl.notificationservice.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.notificationservice.exception.NotificationNotFoundException;
import org.yvl.notificationservice.entity.Notification;
import org.yvl.notificationservice.notification.dto.response.NotificationItemView;
import org.yvl.notificationservice.notification.mapper.NotificationQueryMapper;
import org.yvl.notificationservice.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationQueryMapper mapper;

    public Page<NotificationItemView> getNotifications(
            Long userId,
            Pageable pageable,
            boolean unreadOnly
    ) {
        Page<Notification> notifications;

        if (unreadOnly) {
            notifications = notificationRepository.findByUserIdAndIsRead(userId, false, pageable);
        }  else {
            notifications = notificationRepository.findByUserId(userId, pageable);
        }

        return notifications.map(mapper::toNotificationItemView);
    }

    public NotificationItemView markAsRead(
            Long userId,
            Long notificationId
    ) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId).orElseThrow(() ->
                new NotificationNotFoundException(notificationId));

        if (!notification.getIsRead())  {
            notification.setIsRead(true);
        }

        return mapper.toNotificationItemView(notification);
    }
}
