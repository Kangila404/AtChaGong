package org.example.server.notification.infrastructure.persistence.repository;

import java.util.Optional;
import org.example.server.notification.domain.models.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationSettingJpaRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUserId(Long userId);

    @Modifying
    @Query("delete from NotificationSetting notificationSetting where notificationSetting.userId = :userId")
    void deleteByUserId(Long userId);
}
