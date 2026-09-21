package org.example.server.beverage.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.example.server.beverage.domain.enums.BeverageAcquisitionType;
import org.example.server.beverage.domain.enums.BeverageSaleStatus;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.models.UserBeverage;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserBeverageJpaRepositoryTest {

    @Autowired
    private UserBeverageJpaRepository userBeverageJpaRepository;

    @Autowired
    private SelectedBeverageJpaRepository selectedBeverageJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("선택 음료를 먼저, 나머지는 진열 순서와 음료 ID 순으로 조회한다")
    void findAllByUserIdSortsOwnedBeverages() {
        User user = persistUser("user-sort");
        Beverage later = persistBeverage("later", 20);
        Beverage firstSameOrder = persistBeverage("first", 10);
        Beverage secondSameOrder = persistBeverage("second", 10);
        UserBeverage selectedOwnership = persistOwnership(user, later);
        persistOwnership(user, firstSameOrder);
        persistOwnership(user, secondSameOrder);
        selectedBeverageJpaRepository.saveAndFlush(
            org.example.server.beverage.domain.models.SelectedBeverage.create(user, selectedOwnership)
        );

        List<UserBeverage> result = userBeverageJpaRepository.findAllByUserId(user.getId());

        assertThat(result)
            .extracting(userBeverage -> userBeverage.getBeverage().getId())
            .containsExactly(later.getId(), firstSameOrder.getId(), secondSameOrder.getId());
    }

    @Test
    @DisplayName("같은 사용자는 동일한 음료를 중복 보유할 수 없다")
    void saveDuplicateOwnershipThrowsException() {
        User user = persistUser("user-duplicate");
        Beverage beverage = persistBeverage("ice", 0);
        persistOwnership(user, beverage);

        assertThatThrownBy(() -> persistOwnership(user, beverage))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    private User persistUser(String userId) {
        User user = User.builder()
            .userId(userId)
            .nickname("tester")
            .userRole(UserRole.USER)
            .userStatus(UserStatus.ACTIVE)
            .onboardingCompleted(true)
            .build();
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private Beverage persistBeverage(String name, int displayOrder) {
        Beverage beverage = Beverage.create(
            name,
            "https://example.com/" + name + ".png",
            0L,
            displayOrder,
            BeverageSaleStatus.ON_SALE,
            false,
            null
        );
        entityManager.persist(beverage);
        entityManager.flush();
        return beverage;
    }

    private UserBeverage persistOwnership(User user, Beverage beverage) {
        return userBeverageJpaRepository.saveAndFlush(UserBeverage.create(
            user,
            beverage,
            BeverageAcquisitionType.DEFAULT,
            LocalDateTime.now()
        ));
    }
}
