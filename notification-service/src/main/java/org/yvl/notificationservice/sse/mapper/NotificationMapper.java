package org.yvl.notificationservice.sse.mapper;

import org.mapstruct.Mapper;
import org.yvl.notificationservice.entity.Notification;
import org.yvl.notificationservice.sse.dto.NotificationView;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationView toNotificationView(Notification notification);
}
