package org.example.server.beverage.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
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
        LEFT JOIN SelectedBeverage selectedBeverage
               ON selectedBeverage.userBeverage = userBeverage
        WHERE userBeverage.user.id = :userId
        ORDER BY CASE WHEN selectedBeverage.id IS NULL THEN 1 ELSE 0 END ASC,
                 beverage.displayOrder ASC,
                 beverage.id ASC
    """)
    List<UserBeverage> findAllByUserId(@Param("userId") Long userId);

    Optional<UserBeverage> findByUser_IdAndBeverage_Id(Long userId, Long beverageId);

    boolean existsByUser_IdAndBeverage_Id(Long userId, Long beverageId);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM UserBeverage userBeverage WHERE userBeverage.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
