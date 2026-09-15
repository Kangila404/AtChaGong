package org.example.server.beverage.infrastructure.persistence.repository;

import java.util.Optional;
import org.example.server.beverage.domain.models.SelectedBeverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SelectedBeverageJpaRepository extends JpaRepository<SelectedBeverage, Long> {

    @Query("""
        SELECT selectedBeverage
        FROM SelectedBeverage selectedBeverage
        JOIN FETCH selectedBeverage.userBeverage userBeverage
        JOIN FETCH userBeverage.beverage
        WHERE selectedBeverage.user.id = :userId
    """)
    Optional<SelectedBeverage> findByUserId(@Param("userId") Long userId);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM SelectedBeverage selectedBeverage WHERE selectedBeverage.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
