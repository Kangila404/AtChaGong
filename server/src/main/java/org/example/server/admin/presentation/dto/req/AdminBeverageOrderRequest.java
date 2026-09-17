package org.example.server.admin.presentation.dto.req;

import java.util.List;

public record AdminBeverageOrderRequest(
    List<OrderItem> orders
) {
    public record OrderItem(
        Long beverageId,
        Integer displayOrder
    ) {
    }
}
