package org.example.server.notification.application;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.example.server.notification.domain.enums.DeviceType;
import org.example.server.notification.domain.models.DeviceToken;
import org.example.server.notification.domain.models.NotificationSetting;
import org.example.server.notification.domain.repositories.DeviceTokenRepository;
import org.example.server.notification.domain.repositories.NotificationSettingRepository;
import org.example.server.notification.presentation.dto.req.DeleteDeviceTokenRequest;
import org.example.server.notification.presentation.dto.req.UpdateNotificationSettingRequest;
import org.example.server.notification.presentation.dto.req.UpsertDeviceTokenRequest;
import org.example.server.notification.presentation.dto.res.DeviceTokenResponse;
import org.example.server.notification.presentation.dto.res.NotificationSettingResponse;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final int MAX_DEVICE_TOKEN_LENGTH = 4096;

    private final NotificationSettingRepository notificationSettingRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public NotificationSettingResponse getNotificationSetting(String userId) {
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);

        NotificationSetting notificationSetting = notificationSettingRepository.findByUserId(user.getId())
            .orElseGet(() -> notificationSettingRepository.save(NotificationSetting.createDefault(user.getId())));

        return NotificationSettingResponse.from(notificationSetting);
    }

    @Transactional
    public NotificationSettingResponse updateNotificationSetting(
        String userId,
        UpdateNotificationSettingRequest request
    ) {
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);
        validateUpdateNotificationSettingRequest(request);

        NotificationSetting notificationSetting = notificationSettingRepository.findByUserId(user.getId())
            .orElseGet(() -> notificationSettingRepository.save(NotificationSetting.createDefault(user.getId())));

        notificationSetting.update(
            request.focusStartEnabled(),
            request.focusEndEnabled(),
            request.breakEndEnabled()
        );

        return NotificationSettingResponse.from(notificationSetting);
    }

    @Transactional
    public DeviceTokenResponse upsertDeviceToken(String userId, UpsertDeviceTokenRequest request) {
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);

        String token = validateAndNormalizeToken(request.token());
        DeviceType platform = DeviceType.from(request.platform());
        LocalDateTime now = LocalDateTime.now(SEOUL_ZONE);

        DeviceToken deviceToken = deviceTokenRepository.findByToken(token)
            .map(savedDeviceToken -> {
                savedDeviceToken.reactivate(user.getId(), platform, now);
                return savedDeviceToken;
            })
            .orElseGet(() -> deviceTokenRepository.save(DeviceToken.register(user.getId(), token, platform, now)));

        return DeviceTokenResponse.of(deviceToken, toSeoulOffsetDateTime(deviceToken.getLastUsedAt()));
    }

    @Transactional
    public void deactivateDeviceToken(String userId, DeleteDeviceTokenRequest request) {
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);

        String token = validateAndNormalizeToken(request.token());
        deviceTokenRepository.findByToken(token)
            .filter(deviceToken -> deviceToken.getUserId().equals(user.getId()))
            .ifPresent(DeviceToken::deactivate);
    }

    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    private void validateUserStatus(User user) {
        if (!user.getUserStatus().equals(UserStatus.ACTIVE)) {
            throw new IllegalArgumentException("비활성 유저입니다.");
        }
    }

    private void validateUpdateNotificationSettingRequest(UpdateNotificationSettingRequest request) {
        if (request.focusStartEnabled() == null
            || request.focusEndEnabled() == null
            || request.breakEndEnabled() == null) {
            throw new IllegalArgumentException("알림 설정이 올바르지 않습니다.");
        }
    }

    private String validateAndNormalizeToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("기기 토큰이 올바르지 않습니다.");
        }

        String normalizedToken = token.trim();
        if (normalizedToken.isEmpty() || normalizedToken.length() > MAX_DEVICE_TOKEN_LENGTH) {
            throw new IllegalArgumentException("기기 토큰이 올바르지 않습니다.");
        }
        return normalizedToken;
    }

    private OffsetDateTime toSeoulOffsetDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(SEOUL_ZONE).toOffsetDateTime();
    }
}
