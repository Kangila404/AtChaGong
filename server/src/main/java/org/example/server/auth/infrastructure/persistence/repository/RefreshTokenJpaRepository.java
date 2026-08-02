package org.example.server.auth.infrastructure.persistence.repository;

import org.example.server.auth.domain.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

}
