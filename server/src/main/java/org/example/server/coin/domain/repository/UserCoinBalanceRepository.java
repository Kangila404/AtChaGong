package org.example.server.coin.domain.repository;

import java.util.Optional;
import org.example.server.coin.domain.models.UserCoinBalance;

public interface UserCoinBalanceRepository {

    Optional<UserCoinBalance> findByUserId(Long userId);

    Optional<UserCoinBalance> findWithLockByUserId(Long userId);

    UserCoinBalance save(UserCoinBalance userCoinBalance);
}
