package org.example.server.auth.domain.repositories;

import java.util.Optional;
import org.example.server.auth.domain.models.RefreshToken;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByUserId(Long userId);
}
