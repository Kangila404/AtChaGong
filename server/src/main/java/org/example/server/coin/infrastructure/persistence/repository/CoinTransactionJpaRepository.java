package org.example.server.coin.infrastructure.persistence.repository;

import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.coin.domain.models.CoinTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinTransactionJpaRepository extends JpaRepository<CoinTransaction, Long> {

    boolean existsByUser_IdAndTransactionTypeAndReferenceTypeAndReferenceId(
        Long userId,
        CoinTransactionType transactionType,
        CoinReferenceType referenceType,
        Long referenceId
    );

    Page<CoinTransaction> findByUser_Id(Long userId, Pageable pageable);

    void deleteByUser_Id(Long userId);
}
