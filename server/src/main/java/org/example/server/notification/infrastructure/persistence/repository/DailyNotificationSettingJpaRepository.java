package org.example.server.notification.infrastructure.persistence.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.example.server.notification.domain.models.DailyNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyNotificationSettingJpaRepository extends JpaRepository<DailyNotificationSetting, Long> {
    Optional<DailyNotificationSetting> findByUserId(Long userId);
    List<DailyNotificationSetting> findAllByNotificationTimeAndEnabledTrue(LocalTime notificationTime);
    void deleteByUserId(Long userId);
}
