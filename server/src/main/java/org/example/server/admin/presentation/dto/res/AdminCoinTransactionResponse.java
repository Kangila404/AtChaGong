package org.example.server.admin.presentation.dto.res;

import java.time.LocalDateTime;
import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.coin.domain.models.CoinTransaction;

public record AdminCoinTransactionResponse(
    Long transactionId,
    long amount,
    long balanceAfter,
    CoinTransactionType transactionType,
    CoinReferenceType referenceType,
    Long referenceId,
    LocalDateTime createdAt
) {
    public static AdminCoinTransactionResponse from(CoinTransaction transaction) {
        return new AdminCoinTransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getBalanceAfter(),
            transaction.getTransactionType(),
            transaction.getReferenceType(),
            transaction.getReferenceId(),
            transaction.getCreatedAt()
        );
    }
}
