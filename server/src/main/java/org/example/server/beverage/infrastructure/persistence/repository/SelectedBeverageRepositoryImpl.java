package org.example.server.beverage.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.models.SelectedBeverage;
import org.example.server.beverage.domain.repository.SelectedBeverageRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SelectedBeverageRepositoryImpl implements SelectedBeverageRepository {

    private final SelectedBeverageJpaRepository selectedBeverageJpaRepository;

    @Override
    public Optional<SelectedBeverage> findByUserId(Long userId) {
        return selectedBeverageJpaRepository.findByUserId(userId);
    }

    @Override
    public SelectedBeverage save(SelectedBeverage selectedBeverage) {
        return selectedBeverageJpaRepository.save(selectedBeverage);
    }

    @Override
    public void deleteByUserId(Long userId) {
        selectedBeverageJpaRepository.deleteByUserId(userId);
    }
}
