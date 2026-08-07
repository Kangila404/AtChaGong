package org.example.server.auth.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.domain.models.AuthAccount;
import org.example.server.auth.domain.repositories.AuthAccountRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthAccountRepositoryImpl implements AuthAccountRepository {

    private final AuthAccountJpaRepository authAccountJpaRepository;

    @Override
    public Optional<AuthAccount> findByUserId(Long userId) {
        return Optional.empty();
    }

    @Override
    public Optional<AuthAccount> findByProviderAndProviderId(AuthType provider, String providerId) {
        return authAccountJpaRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public AuthAccount save(AuthAccount authAccount) {
        return authAccountJpaRepository.save(authAccount);
    }
}
