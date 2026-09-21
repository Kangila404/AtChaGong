package org.example.server.beverage.presentation.dto.res;

import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.models.SelectedBeverage;

public record SelectedBeverageResponse(
    Long beverageId,
    String name
) {
    public static SelectedBeverageResponse from(SelectedBeverage selectedBeverage) {
        Beverage beverage = selectedBeverage.getUserBeverage().getBeverage();
        return new SelectedBeverageResponse(
            beverage.getId(),
            beverage.getName()
        );
    }
}
