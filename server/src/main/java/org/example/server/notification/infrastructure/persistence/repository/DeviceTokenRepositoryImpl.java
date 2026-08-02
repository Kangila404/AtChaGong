package org.example.server.notification.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.notification.domain.models.DeviceToken;
import org.example.server.notification.domain.repositories.DeviceTokenRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeviceTokenRepositoryImpl implements DeviceTokenRepository {

    private final DeviceTokenJpaRepository deviceTokenJpaRepository;

    @Override
    public Optional<DeviceToken> findByUserId(Long userId) {
        return Optional.empty();
    }
}
