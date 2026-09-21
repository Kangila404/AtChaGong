package org.example.server.notification.application;

import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.example.server.notification.domain.models.DailyNotificationSendHistory;
import org.example.server.notification.domain.repositories.DailyNotificationSettingRepository;
import org.example.server.notification.domain.repositories.DeviceTokenRepository;
import org.example.server.notification.infrastructure.persistence.repository.DailyNotificationSendHistoryJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DailyNotificationBatchService {
    private static final String TITLE = "앗차공";
    private static final String BODY = "오늘도 집중할 시간이에요!";
    private final DailyNotificationSettingRepository settingRepository;
    private final DailyNotificationSendHistoryJpaRepository historyRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final PushNotificationSender pushNotificationSender;

    @Transactional
    public void sendDueNotifications(LocalDateTime now) {
        LocalTime notificationTime = now.toLocalTime().withSecond(0).withNano(0);
        settingRepository.findAllByNotificationTimeAndEnabledTrue(notificationTime).forEach(setting -> {
            if (historyRepository.existsByUserIdAndNotificationDate(setting.getUserId(), now.toLocalDate())) {
                return;
            }
            historyRepository.save(DailyNotificationSendHistory.create(setting.getUserId(), now.toLocalDate()));
            deviceTokenRepository.findAllByUserIdAndActiveTrue(setting.getUserId())
                .forEach(token -> pushNotificationSender.send(token.getToken(), TITLE, BODY));
        });
    }
}
