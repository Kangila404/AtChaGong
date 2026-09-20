package org.example.server.coin.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.coin.domain.models.UserCoinBalance;
import org.example.server.coin.domain.repository.UserCoinBalanceRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCoinBalanceRepositoryImpl implements UserCoinBalanceRepository {

    private final UserCoinBalanceJpaRepository userCoinBalanceJpaRepository;

    @Override
    public Optional<UserCoinBalance> findByUserId(Long userId) {
        return userCoinBalanceJpaRepository.findByUser_Id(userId);
    }

    @Override
    public Optional<UserCoinBalance> findWithLockByUserId(Long userId) {
        return userCoinBalanceJpaRepository.findWithLockByUser_Id(userId);
    }

    @Override
    public UserCoinBalance save(UserCoinBalance userCoinBalance) {
        return userCoinBalanceJpaRepository.save(userCoinBalance);
    }
}
