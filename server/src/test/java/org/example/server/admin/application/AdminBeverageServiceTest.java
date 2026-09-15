package org.example.server.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.example.server.admin.presentation.dto.req.AdminBeverageCreateRequest;
import org.example.server.admin.presentation.dto.req.AdminBeverageOrderRequest;
import org.example.server.admin.presentation.dto.req.AdminBeverageUpdateRequest;
import org.example.server.beverage.domain.enums.BeverageSaleStatus;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.exception.BeverageErrorCode;
import org.example.server.beverage.exception.BeverageException;
import org.example.server.common.exception.AtchagongException;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminBeverageServiceTest {

    private static final String ADMIN_ID = "admin-1";
    private static final long ADMIN_PK = 1L;
    private static final long BEVERAGE_ID = 10L;

    @InjectMocks
    private AdminBeverageService adminBeverageService;

    @Mock
    private BeverageRepository beverageRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("관리자가 음료를 등록하면 기본 판매 상태는 DRAFT다")
    void createBeverageUsesDraftStatus() {
        AdminBeverageCreateRequest request = new AdminBeverageCreateRequest(
            " latte ",
            "https://example.com/latte.png",
            4500L,
            2,
            OffsetDateTime.now(ZoneOffset.UTC).plusDays(1)
        );
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin()));
        given(beverageRepository.save(any(Beverage.class))).willAnswer(invocation -> invocation.getArgument(0));

        adminBeverageService.createBeverage(ADMIN_ID, request);

        ArgumentCaptor<Beverage> captor = ArgumentCaptor.forClass(Beverage.class);
        verify(beverageRepository).save(captor.capture());
        Beverage saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("latte");
        assertThat(saved.getSaleStatus()).isEqualTo(BeverageSaleStatus.DRAFT);
        assertThat(saved.getPrice()).isEqualTo(4500L);
    }

    @Test
    @DisplayName("관리자가 아닌 사용자는 음료를 등록할 수 없다")
    void createBeverageByNonAdminThrowsException() {
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(user(UserRole.USER)));

        assertThatThrownBy(() -> adminBeverageService.createBeverage(
            ADMIN_ID,
            new AdminBeverageCreateRequest("latte", "https://example.com/latte.png", 4500L, 0, null)
        )).isInstanceOf(AtchagongException.class);
        verify(beverageRepository, never()).save(any());
    }

    @Test
    @DisplayName("기본 음료의 가격과 판매 종료 시각, 판매 종료 상태 변경은 거부한다")
    void updateDefaultBeverageWithProtectedFieldsThrowsException() {
        Beverage defaultBeverage = beverage(true);
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin()));
        given(beverageRepository.findById(BEVERAGE_ID)).willReturn(Optional.of(defaultBeverage));

        assertThatThrownBy(() -> adminBeverageService.updateBeverage(
            ADMIN_ID,
            String.valueOf(BEVERAGE_ID),
            new AdminBeverageUpdateRequest(null, null, 5000L, "ENDED", OffsetDateTime.now(ZoneOffset.UTC).plusDays(1), null)
        ))
            .isInstanceOf(BeverageException.class)
            .extracting("code")
            .isEqualTo(BeverageErrorCode.DEFAULT_BEVERAGE_MODIFICATION_NOT_ALLOWED.name());
    }

    @Test
    @DisplayName("관리자는 음료 판매 상태를 ON_SALE로 변경할 수 있다")
    void updateBeverageChangesSaleStatus() {
        Beverage beverage = Beverage.create(
            "latte",
            "https://example.com/latte.png",
            4500L,
            0,
            BeverageSaleStatus.DRAFT,
            false,
            null
        );
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin()));
        given(beverageRepository.findById(BEVERAGE_ID)).willReturn(Optional.of(beverage));

        adminBeverageService.updateBeverage(
            ADMIN_ID,
            String.valueOf(BEVERAGE_ID),
            new AdminBeverageUpdateRequest(null, null, null, "ON_SALE", null, null)
        );

        assertThat(beverage.getSaleStatus()).isEqualTo(BeverageSaleStatus.ON_SALE);
    }

    @Test
    @DisplayName("순서 변경 대상 중 존재하지 않는 음료가 있으면 어떤 음료도 변경하지 않는다")
    void updateDisplayOrdersWithMissingBeverageDoesNotChangeAnyOrder() {
        Beverage beverage = Beverage.create(
            "latte",
            "https://example.com/latte.png",
            4500L,
            1,
            BeverageSaleStatus.DRAFT,
            false,
            null
        );
        AdminBeverageOrderRequest request = new AdminBeverageOrderRequest(List.of(
            new AdminBeverageOrderRequest.OrderItem(BEVERAGE_ID, 3),
            new AdminBeverageOrderRequest.OrderItem(11L, 4)
        ));
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin()));
        given(beverageRepository.findById(BEVERAGE_ID)).willReturn(Optional.of(beverage));
        given(beverageRepository.findById(11L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminBeverageService.updateDisplayOrders(ADMIN_ID, request))
            .isInstanceOf(BeverageException.class)
            .extracting("code")
            .isEqualTo(BeverageErrorCode.BEVERAGE_NOT_FOUND.name());
        assertThat(beverage.getDisplayOrder()).isEqualTo(1);
    }

    private User admin() {
        return user(UserRole.ADMIN);
    }

    private User user(UserRole role) {
        return User.builder()
            .id(ADMIN_PK)
            .userId(ADMIN_ID)
            .nickname("admin")
            .userRole(role)
            .userStatus(UserStatus.ACTIVE)
            .onboardingCompleted(true)
            .build();
    }

    private Beverage beverage(boolean isDefault) {
        Beverage beverage = org.mockito.Mockito.mock(Beverage.class);
        given(beverage.isDefault()).willReturn(isDefault);
        return beverage;
    }
}
