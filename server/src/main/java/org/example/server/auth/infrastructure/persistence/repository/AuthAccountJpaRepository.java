package org.example.server.auth.infrastructure.persistence.repository;

import java.util.Optional;
import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.domain.models.AuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthAccountJpaRepository extends JpaRepository<AuthAccount,Long> {
    Optional<AuthAccount> findByProviderAndProviderId(AuthType provider, String providerId);
}
