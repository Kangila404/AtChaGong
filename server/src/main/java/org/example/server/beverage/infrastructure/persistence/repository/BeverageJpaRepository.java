package org.example.server.beverage.infrastructure.persistence.repository;

import java.util.List;
import org.example.server.beverage.domain.models.Beverage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeverageJpaRepository extends JpaRepository<Beverage, Long> {
    List<Beverage> findAll();
}
