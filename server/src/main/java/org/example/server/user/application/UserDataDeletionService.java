package org.example.server.user.application;

import lombok.RequiredArgsConstructor;
import org.example.server.attendance.domain.repository.AttendanceRecordRepository;
import org.example.server.auth.domain.repositories.AuthAccountRepository;
import org.example.server.auth.domain.repositories.RefreshTokenRepository;
import org.example.server.beverage.domain.repository.SelectedBeverageRepository;
import org.example.server.beverage.domain.repository.UserBeverageRepository;
import org.example.server.coin.domain.repository.CoinTransactionRepository;
import org.example.server.coin.domain.repository.UserCoinBalanceRepository;
import org.example.server.notification.domain.repositories.DailyNotificationSettingRepository;
import org.example.server.notification.domain.repositories.DeviceTokenRepository;
import org.example.server.notification.domain.repositories.NotificationSettingRepository;
import org.example.server.notification.infrastructure.persistence.repository.DailyNotificationSendHistoryJpaRepository;
import org.example.server.record.domain.repository.FocusRecordRepository;
import org.example.server.timer.domain.repository.TimerSettingRepository;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDataDeletionService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final DailyNotificationSettingRepository dailyNotificationSettingRepository;
    private final DailyNotificationSendHistoryJpaRepository dailyNotificationSendHistoryJpaRepository;
    private final TimerSettingRepository timerSettingRepository;
    private final FocusRecordRepository focusRecordRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final SelectedBeverageRepository selectedBeverageRepository;
    private final UserBeverageRepository userBeverageRepository;
    private final CoinTransactionRepository coinTransactionRepository;
    private final UserCoinBalanceRepository userCoinBalanceRepository;
    private final AuthAccountRepository authAccountRepository;
    private final UserRepository userRepository;

    public void delete(User user) {
        Long userId = user.getId();

        refreshTokenRepository.deleteByUserId(userId);
        deviceTokenRepository.deleteByUserId(userId);
        notificationSettingRepository.deleteByUserId(userId);
        dailyNotificationSettingRepository.deleteByUserId(userId);
        dailyNotificationSendHistoryJpaRepository.deleteByUserId(userId);
        timerSettingRepository.deleteByUserId(userId);
        focusRecordRepository.deleteByUserId(userId);
        attendanceRecordRepository.deleteByUserId(userId);
        selectedBeverageRepository.deleteByUserId(userId);
        userBeverageRepository.deleteByUserId(userId);
        coinTransactionRepository.deleteByUserId(userId);
        userCoinBalanceRepository.deleteByUserId(userId);
        authAccountRepository.deleteByUserId(userId);
        userRepository.delete(user);
    }
}
