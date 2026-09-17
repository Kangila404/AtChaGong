package org.example.server.beverage.presentation.dto.res;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.example.server.beverage.domain.models.Beverage;

public record BeverageSaleResponse(
    Long beverageId,
    String name,
    String imgUrl,
    Long price,
    boolean isLimited,
    OffsetDateTime saleEndsAt
) {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static BeverageSaleResponse from(Beverage beverage) {
        return new BeverageSaleResponse(
            beverage.getId(),
            beverage.getName(),
            beverage.getImgUrl(),
            beverage.getPrice(),
            beverage.isLimited(),
            beverage.getSaleEndsAt() == null
                ? null
                : beverage.getSaleEndsAt().atZone(SEOUL_ZONE).toOffsetDateTime()
        );
    }
}
