package org.example.server.beverage.domain.repository;

import java.util.Optional;
import org.example.server.beverage.domain.models.SelectedBeverage;

public interface SelectedBeverageRepository {
    Optional<SelectedBeverage> findByUserId(Long userId);
    SelectedBeverage save(SelectedBeverage selectedBeverage);
    void deleteByUserId(Long userId);
}
