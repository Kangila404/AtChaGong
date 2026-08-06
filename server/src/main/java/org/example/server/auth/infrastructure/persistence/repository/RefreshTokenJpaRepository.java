package org.example.server.auth.infrastructure.persistence.repository;

import java.util.Optional;
import org.example.server.auth.domain.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    Optional<RefreshToken> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
