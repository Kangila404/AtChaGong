package org.example.server.beverage.presentation.dto.res;

import org.example.server.beverage.domain.models.Beverage;

public record BeverageResponse(
    Long beverageId,
    String name,
    String imgUrl
) {
    public static BeverageResponse from(Beverage beverage) {
        return new BeverageResponse(
            beverage.getId(),
            beverage.getName(),
            beverage.getImgUrl()
        );
    }
}
