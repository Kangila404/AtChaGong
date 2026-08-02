package org.example.server.beverage.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BeverageRepositoryImpl implements BeverageRepository {

    private final BeverageJpaRepository beverageJpaRepository;

    @Override
    public Optional<Beverage> findById(Long id) {
        return Optional.empty();
    }
}
