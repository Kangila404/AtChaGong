package org.example.server.notification.infrastructure.persistence.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.notification.domain.models.DailyNotificationSetting;
import org.example.server.notification.domain.repositories.DailyNotificationSettingRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DailyNotificationSettingRepositoryImpl implements DailyNotificationSettingRepository {
    private final DailyNotificationSettingJpaRepository repository;
    public Optional<DailyNotificationSetting> findByUserId(Long userId) { return repository.findByUserId(userId); }
    public List<DailyNotificationSetting> findAllByNotificationTimeAndEnabledTrue(LocalTime time) { return repository.findAllByNotificationTimeAndEnabledTrue(time); }
    public DailyNotificationSetting save(DailyNotificationSetting setting) { return repository.save(setting); }
    public void deleteByUserId(Long userId) { repository.deleteByUserId(userId); }
}
