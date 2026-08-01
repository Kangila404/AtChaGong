package org.example.server.notification.domain.repositories;

import java.util.Optional;
import org.example.server.notification.domain.models.DeviceToken;

public interface DeviceTokenRepository {
    Optional<DeviceToken> findByUserId(Long userId);
}
