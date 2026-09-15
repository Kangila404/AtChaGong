package org.example.server.beverage.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import org.example.server.beverage.domain.enums.BeverageAcquisitionType;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.models.SelectedBeverage;
import org.example.server.beverage.domain.models.UserBeverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.domain.repository.SelectedBeverageRepository;
import org.example.server.beverage.domain.repository.UserBeverageRepository;
import org.example.server.beverage.exception.BeverageErrorCode;
import org.example.server.beverage.exception.BeverageException;
import org.example.server.beverage.presentation.dto.req.SelectBeverageRequest;
import org.example.server.beverage.presentation.dto.res.SelectedBeverageResponse;
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
class SelectedBeverageServiceTest {

    private static final String USER_ID = "user-1";
    private static final long USER_PK = 1L;

    @InjectMocks
    private SelectedBeverageService selectedBeverageService;

    @Mock
    private SelectedBeverageRepository selectedBeverageRepository;

    @Mock
    private UserBeverageRepository userBeverageRepository;

    @Mock
    private BeverageRepository beverageRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("현재 선택한 음료를 조회한다")
    void getSelectedBeverageReturnsSelection() {
        User user = activeUser();
        SelectedBeverage selection = SelectedBeverage.create(
            user,
            ownership(user, beverageWithDetails(1L, "ice"))
        );
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user));
        given(selectedBeverageRepository.findByUserId(USER_PK)).willReturn(Optional.of(selection));

        SelectedBeverageResponse response = selectedBeverageService.getSelectedBeverage(USER_ID);

        assertThat(response).isEqualTo(new SelectedBeverageResponse(
            1L,
            "ice",
            "https://example.com/ice.png"
        ));
    }

    @Test
    @DisplayName("보유한 음료로 현재 선택을 변경한다")
    void selectBeverageChangesSelection() {
        User user = activeUser();
        Beverage oldBeverage = org.mockito.Mockito.mock(Beverage.class);
        Beverage newBeverage = beverageWithDetails(2L, "latte");
        UserBeverage newOwnership = ownership(user, newBeverage);
        SelectedBeverage selection = SelectedBeverage.create(user, ownership(user, oldBeverage));
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user));
        given(beverageRepository.findById(2L)).willReturn(Optional.of(newBeverage));
        given(userBeverageRepository.findByUserIdAndBeverageId(USER_PK, 2L))
            .willReturn(Optional.of(newOwnership));
        given(selectedBeverageRepository.findByUserId(USER_PK)).willReturn(Optional.of(selection));

        SelectedBeverageResponse response = selectedBeverageService.selectBeverage(
            USER_ID,
            new SelectBeverageRequest(2L)
        );

        assertThat(response)
            .returns(2L, SelectedBeverageResponse::beverageId)
            .matches(ignored -> Objects.equals(selection.getUserBeverage(), newOwnership));
    }

    @Test
    @DisplayName("보유하지 않은 음료는 선택할 수 없다")
    void selectBeverageNotOwnedThrowsException() {
        User user = activeUser();
        Beverage beverage = beverage(2L);
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user));
        given(beverageRepository.findById(2L)).willReturn(Optional.of(beverage));
        given(userBeverageRepository.findByUserIdAndBeverageId(USER_PK, 2L))
            .willReturn(Optional.empty());

        assertThatThrownBy(() -> selectedBeverageService.selectBeverage(
            USER_ID,
            new SelectBeverageRequest(2L)
        ))
            .isInstanceOf(BeverageException.class)
            .extracting("code")
            .isEqualTo(BeverageErrorCode.BEVERAGE_NOT_OWNED.name());
    }

    @Test
    @DisplayName("음료 ID가 없으면 선택할 수 없다")
    void selectBeverageWithoutIdThrowsException() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));

        assertThatThrownBy(() -> selectedBeverageService.selectBeverage(
            USER_ID,
            new SelectBeverageRequest(null)
        ))
            .isInstanceOf(BeverageException.class)
            .extracting("code")
            .isEqualTo(BeverageErrorCode.BEVERAGE_ID_REQUIRED.name());
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

    private UserBeverage ownership(User user, Beverage beverage) {
        return UserBeverage.create(
            user,
            beverage,
            BeverageAcquisitionType.DEFAULT,
            LocalDateTime.now()
        );
    }

    private Beverage beverage(Long id) {
        Beverage beverage = org.mockito.Mockito.mock(Beverage.class);
        given(beverage.getId()).willReturn(id);
        return beverage;
    }

    private Beverage beverageWithDetails(Long id, String name) {
        Beverage beverage = beverage(id);
        given(beverage.getName()).willReturn(name);
        given(beverage.getImgUrl()).willReturn("https://example.com/" + name + ".png");
        return beverage;
    }
}
