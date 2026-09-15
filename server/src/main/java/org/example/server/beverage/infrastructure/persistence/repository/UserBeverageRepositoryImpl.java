package org.example.server.beverage.infrastructure.persistence.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.models.UserBeverage;
import org.example.server.beverage.domain.repository.UserBeverageRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserBeverageRepositoryImpl implements UserBeverageRepository {

    private final UserBeverageJpaRepository userBeverageJpaRepository;

    @Override
    public List<UserBeverage> findAllByUserId(Long userId) {
        return userBeverageJpaRepository.findAllByUserId(userId);
    }

    @Override
    public boolean existsByUserIdAndBeverageId(Long userId, Long beverageId) {
        return userBeverageJpaRepository.existsByUser_IdAndBeverage_Id(userId, beverageId);
    }

    @Override
    public UserBeverage save(UserBeverage userBeverage) {
        return userBeverageJpaRepository.save(userBeverage);
    }

    @Override
    public void deleteByUserId(Long userId) {
        userBeverageJpaRepository.deleteByUserId(userId);
    }
}
