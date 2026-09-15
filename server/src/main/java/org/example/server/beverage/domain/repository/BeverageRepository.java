package org.example.server.beverage.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.enums.BeverageSaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BeverageRepository {
    Optional<Beverage> findById(Long id);
    Optional<Beverage> findDefault();
    List<Beverage> findAll();
    List<Beverage> findAllAvailableForSale(LocalDateTime now);
    Page<Beverage> findAll(Pageable pageable);
    Page<Beverage> findBySaleStatus(BeverageSaleStatus saleStatus, Pageable pageable);
    Beverage save(Beverage beverage);
    boolean existsById(Long id);
}
