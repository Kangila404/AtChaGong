package org.example.server.auth.domain.repositories;

import java.util.Optional;
import org.example.server.auth.domain.models.AuthAccount;

public interface AuthAccountRepository {
    Optional<AuthAccount> findByUserId(Long userId);
}
