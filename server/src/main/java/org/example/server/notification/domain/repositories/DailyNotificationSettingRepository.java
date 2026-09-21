package org.example.server.notification.domain.repositories;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.example.server.notification.domain.models.DailyNotificationSetting;

public interface DailyNotificationSettingRepository {
    Optional<DailyNotificationSetting> findByUserId(Long userId);
    List<DailyNotificationSetting> findAllByNotificationTimeAndEnabledTrue(LocalTime notificationTime);
    DailyNotificationSetting save(DailyNotificationSetting setting);
}
