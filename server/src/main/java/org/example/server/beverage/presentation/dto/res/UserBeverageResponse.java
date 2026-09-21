package org.example.server.beverage.presentation.dto.res;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.models.UserBeverage;

public record UserBeverageResponse(
    Long beverageId,
    String name,
    boolean isSelected,
    OffsetDateTime acquiredAt
) {
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static UserBeverageResponse from(UserBeverage userBeverage, boolean isSelected) {
        Beverage beverage = userBeverage.getBeverage();
        return new UserBeverageResponse(
            beverage.getId(),
            beverage.getName(),
            isSelected,
            userBeverage.getAcquiredAt().atZone(SEOUL_ZONE).toOffsetDateTime()
        );
    }
}
