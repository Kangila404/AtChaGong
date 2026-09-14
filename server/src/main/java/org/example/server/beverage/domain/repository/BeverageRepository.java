package org.example.server.beverage.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.server.beverage.domain.models.Beverage;

public interface BeverageRepository {
    Optional<Beverage> findById(Long id);
    List<Beverage> findAll();
    List<Beverage> findAllAvailableForSale(LocalDateTime now);
    boolean existsById(Long id);
}
