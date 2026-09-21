package org.example.server.user.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUserId(String userId);
    User save(User user);
    long countByUserStatusInAndDeletedAtIsNull(Collection<UserStatus> userStatuses);
    List<User> findAllByDeletedAtIsNull();
}
