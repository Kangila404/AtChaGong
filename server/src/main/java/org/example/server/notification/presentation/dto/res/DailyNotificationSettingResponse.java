package org.example.server.notification.presentation.dto.res;

import java.time.LocalTime;
import org.example.server.notification.domain.models.DailyNotificationSetting;

public record DailyNotificationSettingResponse(LocalTime notificationTime, boolean enabled) {
    public static DailyNotificationSettingResponse from(DailyNotificationSetting setting) {
        return new DailyNotificationSettingResponse(setting.getNotificationTime(), setting.isEnabled());
    }
}
