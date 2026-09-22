package org.example.server.user.infrastructure.persistence.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        return userJpaRepository.findByUserId(userId);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public void delete(User user) { userJpaRepository.delete(user); }

    @Override
    public long countByUserStatusInAndDeletedAtIsNull(Collection<UserStatus> userStatuses) {
        return userJpaRepository.countByUserStatusInAndDeletedAtIsNull(userStatuses);
    }

    @Override
    public List<User> findAllByDeletedAtIsNull() {
        return userJpaRepository.findAllByDeletedAtIsNull();
    }


}
