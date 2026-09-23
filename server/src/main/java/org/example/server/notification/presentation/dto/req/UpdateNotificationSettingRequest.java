package org.example.server.notification.presentation.dto.req;

public record UpdateNotificationSettingRequest(
    Boolean focusTimerEnabled,

    Boolean focusStartEnabled,

    Boolean focusEndEnabled,

    Boolean breakEndEnabled,

    Boolean seasonalBeverageEnabled
) {
}
