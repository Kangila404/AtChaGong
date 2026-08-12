package org.example.server.notification.presentation.dto.req;

import jakarta.validation.constraints.NotNull;

public record UpdateNotificationSettingRequest(
    @NotNull
    Boolean focusStartEnabled,

    @NotNull
    Boolean focusEndEnabled,

    @NotNull
    Boolean breakEndEnabled
) {
}
