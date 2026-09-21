package org.example.server.admin.presentation.dto.res;

import java.util.List;
import org.example.server.coin.domain.models.CoinTransaction;
import org.springframework.data.domain.Page;

public record AdminCoinTransactionPageResponse(
    List<AdminCoinTransactionResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static AdminCoinTransactionPageResponse of(
        Page<CoinTransaction> transactionPage,
        List<AdminCoinTransactionResponse> content
    ) {
        return new AdminCoinTransactionPageResponse(
            content,
            transactionPage.getNumber(),
            transactionPage.getSize(),
            transactionPage.getTotalElements(),
            transactionPage.getTotalPages()
        );
    }
}
