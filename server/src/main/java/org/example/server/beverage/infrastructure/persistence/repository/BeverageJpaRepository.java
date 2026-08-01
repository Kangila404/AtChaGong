package org.example.server.beverage.infrastructure.persistence.repository;

import org.example.server.beverage.domain.models.Beverage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeverageJpaRepository extends JpaRepository<Beverage, Long> {

}
