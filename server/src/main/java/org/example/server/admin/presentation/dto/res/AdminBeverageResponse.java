package org.example.server.admin.presentation.dto.res;

import java.time.OffsetDateTime;
import lombok.Builder;
import org.example.server.beverage.domain.models.Beverage;

@Builder
public record AdminBeverageResponse(
    Long beverageId,
    String name,
    Long price,
    String saleStatus,
    OffsetDateTime saleEndsAt,
    Integer displayOrder,
    boolean isDefault
) {
    public static AdminBeverageResponse of(Beverage beverage, OffsetDateTime saleEndsAt) {
        return AdminBeverageResponse.builder()
            .beverageId(beverage.getId())
            .name(beverage.getName())
            .price(beverage.getPrice())
            .saleStatus(beverage.getSaleStatus().name())
            .saleEndsAt(saleEndsAt)
            .displayOrder(beverage.getDisplayOrder())
            .isDefault(beverage.isDefault())
            .build();
    }
}
