package org.example.server.beverage.infrastructure.persistence.repository;

import java.util.List;
import org.example.server.beverage.domain.models.UserBeverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBeverageJpaRepository extends JpaRepository<UserBeverage, Long> {

    @Query("""
        SELECT userBeverage
        FROM UserBeverage userBeverage
        JOIN FETCH userBeverage.beverage beverage
        WHERE userBeverage.user.id = :userId
        ORDER BY beverage.displayOrder ASC, beverage.id ASC
    """)
    List<UserBeverage> findAllByUserId(@Param("userId") Long userId);

    boolean existsByUser_IdAndBeverage_Id(Long userId, Long beverageId);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM UserBeverage userBeverage WHERE userBeverage.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
