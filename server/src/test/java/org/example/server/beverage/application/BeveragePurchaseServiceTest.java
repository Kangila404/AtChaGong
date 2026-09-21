package org.example.server.beverage.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.example.server.beverage.domain.enums.BeverageSaleStatus;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.domain.repository.UserBeverageRepository;
import org.example.server.beverage.exception.BeverageException;
import org.example.server.coin.application.CoinService;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BeveragePurchaseServiceTest {
    @InjectMocks private BeveragePurchaseService service;
    @Mock private BeverageRepository beverageRepository;
    @Mock private UserBeverageRepository userBeverageRepository;
    @Mock private UserRepository userRepository;
    @Mock private CoinService coinService;

    @Test
    void purchasesOnSaleUnownedBeverage() {
        User user = user(); Beverage beverage = beverage(false);
        given(userRepository.findByUserId("user")).willReturn(Optional.of(user));
        given(beverageRepository.findById(2L)).willReturn(Optional.of(beverage));
        given(userBeverageRepository.existsByUserIdAndBeverageId(1L, 2L)).willReturn(false);
        given(coinService.changeBalance(any(), any(Long.class), any(), any(), any())).willReturn(50L);

        assertThat(service.purchase("user", 2L).balance()).isEqualTo(50L);
        verify(userBeverageRepository).save(any());
    }

    @Test
    void rejectsDefaultBeverageWithoutDeduction() {
        User user = user(); Beverage beverage = beverage(true);
        given(userRepository.findByUserId("user")).willReturn(Optional.of(user));
        given(beverageRepository.findById(2L)).willReturn(Optional.of(beverage));

        assertThatThrownBy(() -> service.purchase("user", 2L)).isInstanceOf(BeverageException.class);
        verify(coinService, never()).changeBalance(any(), any(Long.class), any(), any(), any());
        verify(userBeverageRepository, never()).save(any());
    }

    private Beverage beverage(boolean isDefault) { return Beverage.create("latte", "https://example.com/a.png", 100L, 1, BeverageSaleStatus.ON_SALE, isDefault, null); }
    private User user() { return User.builder().id(1L).userId("user").nickname("t").userStatus(UserStatus.ACTIVE).userRole(UserRole.USER).onboardingCompleted(true).build(); }
}
