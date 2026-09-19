package org.yvl.teamforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
