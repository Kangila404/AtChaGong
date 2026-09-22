package org.example.server.coin.infrastructure.persistence.repository;

import java.util.Optional;
import org.example.server.coin.domain.models.UserCoinBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface UserCoinBalanceJpaRepository extends JpaRepository<UserCoinBalance, Long> {

    Optional<UserCoinBalance> findByUser_Id(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserCoinBalance> findWithLockByUser_Id(Long userId);

    void deleteByUser_Id(Long userId);
}
