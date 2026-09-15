package org.example.server.beverage.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.enums.BeverageSaleStatus;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Optional<Beverage> findDefault() {
        return beverageJpaRepository.findFirstByIsDefaultTrueOrderByIdAsc();
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
    public Page<Beverage> findAll(Pageable pageable) {
        return beverageJpaRepository.findAll(pageable);
    }

    @Override
    public Page<Beverage> findBySaleStatus(BeverageSaleStatus saleStatus, Pageable pageable) {
        return beverageJpaRepository.findBySaleStatus(saleStatus, pageable);
    }

    @Override
    public Beverage save(Beverage beverage) {
        return beverageJpaRepository.save(beverage);
    }

    @Override
    public boolean existsById(Long id) {
        return beverageJpaRepository.existsById(id);
    }
}
