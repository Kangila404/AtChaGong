package org.example.server.auth.domain.repositories;

import java.util.Optional;
import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.domain.models.AuthAccount;

public interface AuthAccountRepository {
    Optional<AuthAccount> findByUserId(Long userId);

    Optional<AuthAccount> findByProviderAndProviderId(AuthType provider, String providerId);

    AuthAccount save(AuthAccount authAccount);
}
