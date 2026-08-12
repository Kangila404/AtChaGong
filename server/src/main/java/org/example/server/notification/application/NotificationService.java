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
import org.example.server.notification.exception.NotificationErrorCode;
import org.example.server.notification.exception.NotificationException;
import org.example.server.notification.presentation.dto.req.DeleteDeviceTokenRequest;
import org.example.server.notification.presentation.dto.req.UpdateNotificationSettingRequest;
import org.example.server.notification.presentation.dto.req.UpsertDeviceTokenRequest;
import org.example.server.notification.presentation.dto.res.DeviceTokenResponse;
import org.example.server.notification.presentation.dto.res.NotificationSettingResponse;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final int MAX_DEVICE_TOKEN_LENGTH = 512;

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
        validateUpsertDeviceTokenRequest(request);

        String token = validateAndNormalizeToken(request.token());
        DeviceType platform = parsePlatform(request.platform());
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
        validateDeleteDeviceTokenRequest(request);

        String token = validateAndNormalizeToken(request.token());
        deviceTokenRepository.findByToken(token)
            .filter(deviceToken -> deviceToken.getUserId().equals(user.getId()))
            .ifPresent(DeviceToken::deactivate);
    }

    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private void validateUserStatus(User user) {
        switch (user.getUserStatus()) {
            case WITHDRAWN -> throw new UserException(UserErrorCode.WITHDRAWN_USER);
            case SUSPENDED -> throw new UserException(UserErrorCode.SUSPENDED_USER);
            case ACTIVE -> { }
        }
    }

    private void validateUpdateNotificationSettingRequest(UpdateNotificationSettingRequest request) {
        if (request == null
            || request.focusStartEnabled() == null
            || request.focusEndEnabled() == null
            || request.breakEndEnabled() == null) {
            throw new NotificationException(NotificationErrorCode.INVALID_NOTIFICATION_SETTING);
        }
    }

    private void validateUpsertDeviceTokenRequest(UpsertDeviceTokenRequest request) {
        if (request == null) {
            throw new NotificationException(NotificationErrorCode.INVALID_DEVICE_TOKEN);
        }
    }

    private void validateDeleteDeviceTokenRequest(DeleteDeviceTokenRequest request) {
        if (request == null) {
            throw new NotificationException(NotificationErrorCode.INVALID_DEVICE_TOKEN);
        }
    }

    private String validateAndNormalizeToken(String token) {
        if (token == null) {
            throw new NotificationException(NotificationErrorCode.INVALID_DEVICE_TOKEN);
        }

        String normalizedToken = token.trim();
        if (normalizedToken.isEmpty() || normalizedToken.length() > MAX_DEVICE_TOKEN_LENGTH) {
            throw new NotificationException(NotificationErrorCode.INVALID_DEVICE_TOKEN);
        }
        return normalizedToken;
    }

    private DeviceType parsePlatform(String platform) {
        try {
            return DeviceType.from(platform);
        } catch (IllegalArgumentException exception) {
            throw new NotificationException(NotificationErrorCode.INVALID_PLATFORM);
        }
    }

    private OffsetDateTime toSeoulOffsetDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(SEOUL_ZONE).toOffsetDateTime();
    }
}
