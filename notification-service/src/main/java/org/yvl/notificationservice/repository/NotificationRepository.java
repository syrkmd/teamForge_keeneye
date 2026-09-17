package org.yvl.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.notificationservice.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
