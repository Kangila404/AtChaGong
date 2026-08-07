package org.example.server.notification.presentation.dto.res;

import java.time.OffsetDateTime;
import org.example.server.notification.domain.models.DeviceToken;

public record DeviceTokenResponse(
    Long deviceTokenId,
    String platform,
    boolean active,
    OffsetDateTime lastUsedAt
) {
    public static DeviceTokenResponse of(DeviceToken deviceToken, OffsetDateTime lastUsedAt) {
        return new DeviceTokenResponse(
            deviceToken.getId(),
            deviceToken.getPlatform().toResponseValue(),
            deviceToken.isActive(),
            lastUsedAt
        );
    }
}
