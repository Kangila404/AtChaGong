package org.example.server.notification.domain.repositories;

import java.util.Optional;
import java.util.List;
import org.example.server.notification.domain.models.DeviceToken;

public interface DeviceTokenRepository {
    Optional<DeviceToken> findByUserId(Long userId);
    Optional<DeviceToken> findByToken(String token);
    List<DeviceToken> findAllByUserIdAndActiveTrue(Long userId);
    DeviceToken save(DeviceToken deviceToken);
    void deleteByUserId(Long userId);
}
