package org.example.server.notification.presentation.dto.res;

import org.example.server.notification.domain.models.NotificationSetting;

public record NotificationSettingResponse(
    boolean focusStartEnabled,
    boolean focusEndEnabled,
    boolean breakEndEnabled
) {
    public static NotificationSettingResponse from(NotificationSetting notificationSetting) {
        return new NotificationSettingResponse(
            notificationSetting.isFocusStartEnabled(),
            notificationSetting.isFocusEndEnabled(),
            notificationSetting.isBreakEndEnabled()
        );
    }
}
