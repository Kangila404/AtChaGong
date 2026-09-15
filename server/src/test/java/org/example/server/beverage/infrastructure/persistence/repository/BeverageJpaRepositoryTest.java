package org.example.server.beverage.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.example.server.beverage.domain.enums.BeverageSaleStatus;
import org.example.server.beverage.domain.models.Beverage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BeverageJpaRepositoryTest {

    @Autowired
    private BeverageJpaRepository beverageJpaRepository;

    @Test
    @DisplayName("판매 가능한 음료만 진열 순서와 ID 순으로 조회한다")
    void findAllAvailableForSaleFiltersAndSortsBeverages() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 15, 12, 0);
        Beverage laterDisplayOrder = save("later", 20, BeverageSaleStatus.ON_SALE, null);
        Beverage firstSameOrder = save("first-same-order", 10, BeverageSaleStatus.ON_SALE, null);
        Beverage secondSameOrder = save(
            "second-same-order",
            10,
            BeverageSaleStatus.ON_SALE,
            now.plusDays(1)
        );
        save("draft", 0, BeverageSaleStatus.DRAFT, null);
        save("ended", 0, BeverageSaleStatus.ENDED, null);
        save("expired", 0, BeverageSaleStatus.ON_SALE, now.minusSeconds(1));
        save("ends-now", 0, BeverageSaleStatus.ON_SALE, now);

        List<Beverage> result = beverageJpaRepository.findAllAvailableForSale(
            BeverageSaleStatus.ON_SALE,
            now
        );

        assertThat(result)
            .extracting(Beverage::getId)
            .containsExactly(firstSameOrder.getId(), secondSameOrder.getId(), laterDisplayOrder.getId());
    }

    private Beverage save(
        String name,
        int displayOrder,
        BeverageSaleStatus status,
        LocalDateTime saleEndsAt
    ) {
        return beverageJpaRepository.saveAndFlush(Beverage.create(
            name,
            "https://example.com/" + name + ".png",
            0L,
            displayOrder,
            status,
            false,
            saleEndsAt
        ));
    }
}
