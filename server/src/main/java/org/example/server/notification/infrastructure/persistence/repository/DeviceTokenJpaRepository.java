package org.example.server.notification.infrastructure.persistence.repository;

import java.util.Optional;
import org.example.server.notification.domain.models.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceTokenJpaRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByUserId(Long userId);
    Optional<DeviceToken> findByToken(String token);
}
