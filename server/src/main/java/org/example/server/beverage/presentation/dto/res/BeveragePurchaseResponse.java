package org.example.server.beverage.presentation.dto.res;

public record BeveragePurchaseResponse(
    Long beverageId,
    String name,
    long paidCoin,
    long balance
) {
}
