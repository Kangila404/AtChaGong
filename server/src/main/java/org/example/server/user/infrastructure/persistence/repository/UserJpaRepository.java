package org.example.server.user.infrastructure.persistence.repository;

import java.util.Collection;
import java.util.Optional;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByUserId(String userId);
    long countByUserStatusInAndDeletedAtIsNull(Collection<UserStatus> userStatuses);
}
