package org.example.server.beverage.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.enums.BeverageSaleStatus;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BeverageRepositoryImpl implements BeverageRepository {

    private final BeverageJpaRepository beverageJpaRepository;

    @Override
    public Optional<Beverage> findById(Long id) {
        return beverageJpaRepository.findById(id);
    }

    @Override
    public List<Beverage> findAll() {
        return beverageJpaRepository.findAll();
    }

    @Override
    public List<Beverage> findAllAvailableForSale(LocalDateTime now) {
        return beverageJpaRepository.findAllAvailableForSale(BeverageSaleStatus.ON_SALE, now);
    }

    @Override
    public boolean existsById(Long id) {
        return beverageJpaRepository.existsById(id);
    }
}
