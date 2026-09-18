package org.example.server.coin.domain.repository;

import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.coin.domain.models.CoinTransaction;

public interface CoinTransactionRepository {

    boolean existsByUserIdAndTransactionTypeAndReferenceTypeAndReferenceId(
        Long userId,
        CoinTransactionType transactionType,
        CoinReferenceType referenceType,
        Long referenceId
    );

    CoinTransaction save(CoinTransaction coinTransaction);
}
