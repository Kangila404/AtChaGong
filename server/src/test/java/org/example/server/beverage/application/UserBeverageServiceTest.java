package org.example.server.beverage.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.example.server.beverage.domain.enums.BeverageAcquisitionType;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.models.UserBeverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.domain.repository.SelectedBeverageRepository;
import org.example.server.beverage.domain.repository.UserBeverageRepository;
import org.example.server.beverage.presentation.dto.res.UserBeverageResponse;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserBeverageServiceTest {

    private static final String USER_ID = "user-1";
    private static final long USER_PK = 1L;

    @InjectMocks
    private UserBeverageService userBeverageService;

    @Mock
    private UserBeverageRepository userBeverageRepository;

    @Mock
    private BeverageRepository beverageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SelectedBeverageRepository selectedBeverageRepository;

    @Test
    @DisplayName("사용자가 보유한 음료 목록을 반환한다")
    void getUserBeveragesReturnsOwnedBeverages() {
        User user = activeUser();
        Beverage beverage = beverageWithDetails(2L, "latte");
        LocalDateTime acquiredAt = LocalDateTime.of(2026, 9, 15, 12, 0);
        UserBeverage ownedBeverage = UserBeverage.create(
            user,
            beverage,
            BeverageAcquisitionType.PURCHASE,
            acquiredAt
        );
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user));
        given(userBeverageRepository.findAllByUserId(USER_PK)).willReturn(List.of(ownedBeverage));

        List<UserBeverageResponse> response = userBeverageService.getUserBeverages(USER_ID);

        assertThat(response).containsExactly(new UserBeverageResponse(
            2L,
            "latte",
            false,
            acquiredAt.atOffset(ZoneOffset.ofHours(9))
        ));
    }

    @Test
    @DisplayName("보유하지 않은 기본 음료를 지급한다")
    void grantDefaultBeverageSavesOwnership() {
        User user = activeUser();
        Beverage defaultBeverage = beverage(1L);
        AtomicReference<UserBeverage> savedOwnership = new AtomicReference<>();
        given(beverageRepository.findDefault()).willReturn(Optional.of(defaultBeverage));
        given(userBeverageRepository.findByUserIdAndBeverageId(USER_PK, 1L))
            .willReturn(Optional.empty());
        given(userBeverageRepository.save(any(UserBeverage.class))).willAnswer(invocation -> {
            UserBeverage userBeverage = invocation.getArgument(0);
            savedOwnership.set(userBeverage);
            return userBeverage;
        });

        userBeverageService.grantDefaultBeverage(user);

        assertThat(savedOwnership.get())
            .returns(user, UserBeverage::getUser)
            .returns(defaultBeverage, UserBeverage::getBeverage)
            .returns(BeverageAcquisitionType.DEFAULT, UserBeverage::getAcquisitionType)
            .matches(userBeverage -> userBeverage.getAcquiredAt() != null);
    }

    @Test
    @DisplayName("이미 기본 음료를 보유하면 중복 지급하지 않는다")
    void grantDefaultBeverageDoesNotSaveDuplicate() {
        User user = activeUser();
        Beverage defaultBeverage = beverage(1L);
        given(beverageRepository.findDefault()).willReturn(Optional.of(defaultBeverage));
        UserBeverage existingOwnership = UserBeverage.create(
            user,
            defaultBeverage,
            BeverageAcquisitionType.DEFAULT,
            LocalDateTime.now()
        );
        given(userBeverageRepository.findByUserIdAndBeverageId(USER_PK, 1L))
            .willReturn(Optional.of(existingOwnership));

        userBeverageService.grantDefaultBeverage(user);

        verify(userBeverageRepository, never()).save(any());
    }

    private User activeUser() {
        return User.builder()
            .id(USER_PK)
            .userId(USER_ID)
            .nickname("tester")
            .userRole(UserRole.USER)
            .userStatus(UserStatus.ACTIVE)
            .onboardingCompleted(true)
            .build();
    }

    private Beverage beverage(Long id) {
        Beverage beverage = org.mockito.Mockito.mock(Beverage.class);
        given(beverage.getId()).willReturn(id);
        return beverage;
    }

    private Beverage beverageWithDetails(Long id, String name) {
        Beverage beverage = beverage(id);
        given(beverage.getName()).willReturn(name);
        return beverage;
    }
}
