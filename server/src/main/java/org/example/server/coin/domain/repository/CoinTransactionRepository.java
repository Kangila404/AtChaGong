package org.example.server.coin.domain.repository;

import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.coin.domain.models.CoinTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CoinTransactionRepository {

    boolean existsByUserIdAndTransactionTypeAndReferenceTypeAndReferenceId(
        Long userId,
        CoinTransactionType transactionType,
        CoinReferenceType referenceType,
        Long referenceId
    );

    CoinTransaction save(CoinTransaction coinTransaction);

    Page<CoinTransaction> findByUserId(Long userId, Pageable pageable);
}
