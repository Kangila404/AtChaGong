package org.example.server.beverage.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.example.server.beverage.domain.enums.BeverageSaleStatus;
import org.example.server.beverage.domain.models.Beverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BeverageJpaRepository extends JpaRepository<Beverage, Long> {
    @Query("""
        SELECT beverage
        FROM Beverage beverage
        WHERE beverage.saleStatus = :saleStatus
          AND (beverage.saleEndsAt IS NULL OR beverage.saleEndsAt > :now)
        ORDER BY beverage.displayOrder ASC, beverage.id ASC
    """)
    List<Beverage> findAllAvailableForSale(
        @Param("saleStatus") BeverageSaleStatus saleStatus,
        @Param("now") LocalDateTime now
    );
}
