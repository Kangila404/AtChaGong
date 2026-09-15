package org.example.server.beverage.domain.repository;

import java.util.List;
import java.util.Optional;
import org.example.server.beverage.domain.models.UserBeverage;

public interface UserBeverageRepository {
    List<UserBeverage> findAllByUserId(Long userId);
    Optional<UserBeverage> findByUserIdAndBeverageId(Long userId, Long beverageId);
    boolean existsByUserIdAndBeverageId(Long userId, Long beverageId);
    UserBeverage save(UserBeverage userBeverage);
    void deleteByUserId(Long userId);
}
