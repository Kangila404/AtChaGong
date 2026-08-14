package org.example.server.notification.domain.repositories;

import java.util.Optional;
import org.example.server.notification.domain.models.NotificationSetting;

public interface NotificationSettingRepository {
    Optional<NotificationSetting> findByUserId(Long userId);
    NotificationSetting save(NotificationSetting notificationSetting);
}
