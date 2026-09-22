package org.example.server.user.application;

import static org.mockito.Mockito.inOrder;

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
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDataDeletionServiceTest {

    private static final Long USER_ID = 1L;

    @InjectMocks
    private UserDataDeletionService userDataDeletionService;

    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private DeviceTokenRepository deviceTokenRepository;
    @Mock private NotificationSettingRepository notificationSettingRepository;
    @Mock private DailyNotificationSettingRepository dailyNotificationSettingRepository;
    @Mock private DailyNotificationSendHistoryJpaRepository dailyNotificationSendHistoryJpaRepository;
    @Mock private TimerSettingRepository timerSettingRepository;
    @Mock private FocusRecordRepository focusRecordRepository;
    @Mock private AttendanceRecordRepository attendanceRecordRepository;
    @Mock private SelectedBeverageRepository selectedBeverageRepository;
    @Mock private UserBeverageRepository userBeverageRepository;
    @Mock private CoinTransactionRepository coinTransactionRepository;
    @Mock private UserCoinBalanceRepository userCoinBalanceRepository;
    @Mock private AuthAccountRepository authAccountRepository;
    @Mock private UserRepository userRepository;

    @Test
    void deletesAllUserRelatedDataBeforeDeletingUser() {
        User user = User.builder()
            .id(USER_ID)
            .userId("user-1")
            .nickname("tester")
            .userStatus(UserStatus.ACTIVE)
            .userRole(UserRole.USER)
            .build();

        userDataDeletionService.delete(user);

        InOrder inOrder = inOrder(
            refreshTokenRepository, deviceTokenRepository, notificationSettingRepository,
            dailyNotificationSettingRepository, dailyNotificationSendHistoryJpaRepository,
            timerSettingRepository, focusRecordRepository, attendanceRecordRepository,
            selectedBeverageRepository, userBeverageRepository, coinTransactionRepository,
            userCoinBalanceRepository, authAccountRepository, userRepository
        );
        inOrder.verify(refreshTokenRepository).deleteByUserId(USER_ID);
        inOrder.verify(deviceTokenRepository).deleteByUserId(USER_ID);
        inOrder.verify(notificationSettingRepository).deleteByUserId(USER_ID);
        inOrder.verify(dailyNotificationSettingRepository).deleteByUserId(USER_ID);
        inOrder.verify(dailyNotificationSendHistoryJpaRepository).deleteByUserId(USER_ID);
        inOrder.verify(timerSettingRepository).deleteByUserId(USER_ID);
        inOrder.verify(focusRecordRepository).deleteByUserId(USER_ID);
        inOrder.verify(attendanceRecordRepository).deleteByUserId(USER_ID);
        inOrder.verify(selectedBeverageRepository).deleteByUserId(USER_ID);
        inOrder.verify(userBeverageRepository).deleteByUserId(USER_ID);
        inOrder.verify(coinTransactionRepository).deleteByUserId(USER_ID);
        inOrder.verify(userCoinBalanceRepository).deleteByUserId(USER_ID);
        inOrder.verify(authAccountRepository).deleteByUserId(USER_ID);
        inOrder.verify(userRepository).delete(user);
    }
}
