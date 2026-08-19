package org.example.server.auth.infrastructure.persistence.repository;

import java.util.Optional;
import org.example.server.auth.domain.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    Optional<RefreshToken> findByUserId(Long userId);

    @Modifying
    @Query("delete from RefreshToken refreshToken where refreshToken.userId = :userId")
    void deleteByUserId(Long userId);
}
