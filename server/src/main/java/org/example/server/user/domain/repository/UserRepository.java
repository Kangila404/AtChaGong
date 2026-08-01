package org.example.server.user.domain.repository;

import java.util.Optional;
import org.example.server.user.domain.models.User;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUserId(String userId);
}
