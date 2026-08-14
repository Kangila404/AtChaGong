package org.example.server.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.example.server.notification.domain.enums.DeviceType;
import org.example.server.notification.domain.models.DeviceToken;
import org.example.server.notification.domain.models.NotificationSetting;
import org.example.server.notification.domain.repositories.DeviceTokenRepository;
import org.example.server.notification.domain.repositories.NotificationSettingRepository;
import org.example.server.notification.exception.NotificationErrorCode;
import org.example.server.notification.exception.NotificationException;
import org.example.server.notification.presentation.dto.req.DeleteDeviceTokenRequest;
import org.example.server.notification.presentation.dto.req.UpdateNotificationSettingRequest;
import org.example.server.notification.presentation.dto.req.UpsertDeviceTokenRequest;
import org.example.server.notification.presentation.dto.res.DeviceTokenResponse;
import org.example.server.notification.presentation.dto.res.NotificationSettingResponse;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private static final String USER_ID = "user-1";
    private static final long USER_PK = 1L;

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationSettingRepository notificationSettingRepository;

    @Mock
    private DeviceTokenRepository deviceTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("알림 설정이 없으면 기본 설정을 생성해서 반환한다")
    void getNotificationSettingCreatesDefaultSetting() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));
        given(notificationSettingRepository.findByUserId(USER_PK)).willReturn(Optional.empty());
        given(notificationSettingRepository.save(any(NotificationSetting.class)))
            .willAnswer(invocation -> invocation.getArgument(0));

        NotificationSettingResponse response = notificationService.getNotificationSetting(USER_ID);

        assertThat(response.focusStartEnabled()).isTrue();
        assertThat(response.focusEndEnabled()).isTrue();
        assertThat(response.breakEndEnabled()).isTrue();
        verify(notificationSettingRepository).save(any(NotificationSetting.class));
    }

    @Test
    @DisplayName("알림 설정 값을 수정한다")
    void updateNotificationSettingUpdatesSetting() {
        NotificationSetting setting = NotificationSetting.createDefault(USER_PK);
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));
        given(notificationSettingRepository.findByUserId(USER_PK)).willReturn(Optional.of(setting));

        NotificationSettingResponse response = notificationService.updateNotificationSetting(
            USER_ID,
            new UpdateNotificationSettingRequest(false, true, false)
        );

        assertThat(response.focusStartEnabled()).isFalse();
        assertThat(response.focusEndEnabled()).isTrue();
        assertThat(response.breakEndEnabled()).isFalse();
        assertThat(setting.isFocusStartEnabled()).isFalse();
    }

    @Test
    @DisplayName("디바이스 토큰을 새로 등록한다")
    void upsertDeviceTokenRegistersNewToken() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));
        given(deviceTokenRepository.findByToken("token-1")).willReturn(Optional.empty());
        given(deviceTokenRepository.save(any(DeviceToken.class))).willAnswer(invocation -> invocation.getArgument(0));

        DeviceTokenResponse response = notificationService.upsertDeviceToken(
            USER_ID,
            new UpsertDeviceTokenRequest(" token-1 ", "ios")
        );

        assertThat(response.platform()).isEqualTo("ios");
        assertThat(response.active()).isTrue();
        verify(deviceTokenRepository).save(any(DeviceToken.class));
    }

    @Test
    @DisplayName("사용자 소유 토큰을 비활성화한다")
    void deactivateDeviceTokenDeactivatesOwnToken() {
        DeviceToken deviceToken = DeviceToken.register(USER_PK, "token-1", DeviceType.ANDROID, java.time.LocalDateTime.now());
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));
        given(deviceTokenRepository.findByToken("token-1")).willReturn(Optional.of(deviceToken));

        notificationService.deactivateDeviceToken(USER_ID, new DeleteDeviceTokenRequest("token-1"));

        assertThat(deviceToken.isActive()).isFalse();
    }

    @Test
    @DisplayName("플랫폼 값이 올바르지 않으면 디바이스 토큰을 등록할 수 없다")
    void upsertDeviceTokenWithInvalidPlatformThrowsException() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));

        assertThatThrownBy(() -> notificationService.upsertDeviceToken(
            USER_ID,
            new UpsertDeviceTokenRequest("token-1", "web")
        ))
            .isInstanceOf(NotificationException.class)
            .extracting("code")
            .isEqualTo(NotificationErrorCode.INVALID_PLATFORM.name());
        verify(deviceTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("탈퇴한 사용자는 알림 설정을 조회할 수 없다")
    void getNotificationSettingWithWithdrawnUserThrowsException() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user(UserStatus.WITHDRAWN)));

        assertThatThrownBy(() -> notificationService.getNotificationSetting(USER_ID))
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.WITHDRAWN_USER.name());
    }

    private User activeUser() {
        return user(UserStatus.ACTIVE);
    }

    private User user(UserStatus status) {
        return User.builder()
            .id(USER_PK)
            .userId(USER_ID)
            .nickname("tester")
            .userStatus(status)
            .userRole(UserRole.USER)
            .onboardingCompleted(true)
            .build();
    }
}
