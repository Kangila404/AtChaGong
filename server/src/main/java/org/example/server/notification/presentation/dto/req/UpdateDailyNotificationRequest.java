package org.example.server.notification.presentation.dto.req;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record UpdateDailyNotificationRequest(@NotNull LocalTime notificationTime, @NotNull Boolean enabled) {
}
