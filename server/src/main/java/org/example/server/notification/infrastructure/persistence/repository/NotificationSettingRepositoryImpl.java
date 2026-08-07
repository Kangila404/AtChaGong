package org.example.server.notification.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.notification.domain.models.NotificationSetting;
import org.example.server.notification.domain.repositories.NotificationSettingRepository;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class NotificationSettingRepositoryImpl implements NotificationSettingRepository {

    private final NotificationSettingJpaRepository notificationSettingJpaRepository;

    @Override
    public Optional<NotificationSetting> findByUserId(Long userId) {
        return notificationSettingJpaRepository.findByUserId(userId);
    }

    @Override
    public NotificationSetting save(NotificationSetting notificationSetting) {
        return notificationSettingJpaRepository.save(notificationSetting);
    }
}
