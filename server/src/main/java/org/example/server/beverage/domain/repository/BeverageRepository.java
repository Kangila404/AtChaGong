package org.example.server.beverage.domain.repository;

import java.util.Optional;
import org.example.server.beverage.domain.models.Beverage;

public interface BeverageRepository {
    Optional<Beverage> findById(Long id);
}
