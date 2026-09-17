package org.yvl.notificationservice.notification.mapper;

import org.mapstruct.Mapper;
import org.yvl.notificationservice.entity.Notification;
import org.yvl.notificationservice.notification.dto.response.NotificationItemView;

@Mapper(componentModel = "spring")
public interface NotificationQueryMapper {

    NotificationItemView toNotificationItemView(Notification notification);
}
