package org.example.server.coin.infrastructure.persistence.repository;

import java.util.Optional;
import org.example.server.coin.domain.models.UserCoinBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCoinBalanceJpaRepository extends JpaRepository<UserCoinBalance, Long> {

    Optional<UserCoinBalance> findByUser_Id(Long userId);
}
