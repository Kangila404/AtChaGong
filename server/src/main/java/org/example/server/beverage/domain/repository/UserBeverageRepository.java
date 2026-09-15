package org.example.server.beverage.domain.repository;

import java.util.List;
import org.example.server.beverage.domain.models.UserBeverage;

public interface UserBeverageRepository {
    List<UserBeverage> findAllByUserId(Long userId);
    boolean existsByUserIdAndBeverageId(Long userId, Long beverageId);
    UserBeverage save(UserBeverage userBeverage);
    void deleteByUserId(Long userId);
}
