package org.example.server.notification.infrastructure.persistence.repository;

import org.example.server.notification.domain.models.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingJpaRepository extends JpaRepository<NotificationSetting, Long> {

}
