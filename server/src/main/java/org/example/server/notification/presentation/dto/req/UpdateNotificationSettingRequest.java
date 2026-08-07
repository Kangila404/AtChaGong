package org.example.server.notification.presentation.dto.req;

public record UpdateNotificationSettingRequest(
    Boolean focusStartEnabled,
    Boolean focusEndEnabled,
    Boolean breakEndEnabled
) {
}
