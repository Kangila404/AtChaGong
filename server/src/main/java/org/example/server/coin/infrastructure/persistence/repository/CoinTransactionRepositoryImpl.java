package org.example.server.coin.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.coin.domain.models.CoinTransaction;
import org.example.server.coin.domain.repository.CoinTransactionRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CoinTransactionRepositoryImpl implements CoinTransactionRepository {

    private final CoinTransactionJpaRepository coinTransactionJpaRepository;

    @Override
    public boolean existsByUserIdAndTransactionTypeAndReferenceTypeAndReferenceId(
        Long userId,
        CoinTransactionType transactionType,
        CoinReferenceType referenceType,
        Long referenceId
    ) {
        return coinTransactionJpaRepository
            .existsByUser_IdAndTransactionTypeAndReferenceTypeAndReferenceId(
                userId,
                transactionType,
                referenceType,
                referenceId
            );
    }

    @Override
    public CoinTransaction save(CoinTransaction coinTransaction) {
        return coinTransactionJpaRepository.save(coinTransaction);
    }
}
