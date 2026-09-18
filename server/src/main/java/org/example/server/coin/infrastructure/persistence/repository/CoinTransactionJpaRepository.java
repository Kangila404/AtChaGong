package org.example.server.coin.infrastructure.persistence.repository;

import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.coin.domain.models.CoinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinTransactionJpaRepository extends JpaRepository<CoinTransaction, Long> {

    boolean existsByUser_IdAndTransactionTypeAndReferenceTypeAndReferenceId(
        Long userId,
        CoinTransactionType transactionType,
        CoinReferenceType referenceType,
        Long referenceId
    );
}
