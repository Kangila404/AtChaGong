package org.example.server.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.server.admin.presentation.dto.req.AdminCoinTransactionPageRequest;
import org.example.server.admin.presentation.dto.res.AdminCoinTransactionPageResponse;
import org.example.server.admin.presentation.dto.req.AdminUpdateUserStatusRequest;
import org.example.server.admin.presentation.dto.res.AdminUpdateStatusResponse;
import org.example.server.admin.presentation.dto.res.AdminUserResponse;
import org.example.server.admin.presentation.dto.res.AdminUserSummaryResponse;
import org.example.server.admin.presentation.dto.res.AdminUsersResponse;
import org.example.server.beverage.domain.repository.SelectedBeverageRepository;
import org.example.server.beverage.domain.repository.UserBeverageRepository;
import org.example.server.common.exception.AtchagongException;
import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.coin.domain.models.CoinTransaction;
import org.example.server.coin.domain.repository.CoinTransactionRepository;
import org.example.server.record.domain.models.FocusRecord;
import org.example.server.record.domain.repository.FocusRecordRepository;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    private static final String ADMIN_ID = "admin-1";
    private static final String USER_ID = "user-1";
    private static final long ADMIN_PK = 1L;
    private static final long USER_PK = 2L;

    @InjectMocks
    private AdminUserService adminUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FocusRecordRepository focusRecordRepository;

    @Mock
    private UserBeverageRepository userBeverageRepository;

    @Mock
    private SelectedBeverageRepository selectedBeverageRepository;

    @Mock
    private CoinTransactionRepository coinTransactionRepository;

    @Test
    @DisplayName("관리자는 전체 사용자 요약을 조회할 수 있다")
    void getUserSummaryReturnsTotalUserCount() {
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(
            Optional.of(user(ADMIN_PK, ADMIN_ID, UserRole.ADMIN, UserStatus.ACTIVE))
        );
        given(userRepository.countByUserStatusInAndDeletedAtIsNull(org.mockito.ArgumentMatchers.anyCollection()))
            .willReturn(12L);

        AdminUserSummaryResponse response = adminUserService.getUserSummary(ADMIN_ID);

        assertThat(response.totalUserCount()).isEqualTo(12L);
    }

    @Test
    @DisplayName("관리자가 아니면 사용자 요약을 조회할 수 없다")
    void getUserSummaryWithNonAdminThrowsException() {
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(
            Optional.of(user(ADMIN_PK, ADMIN_ID, UserRole.USER, UserStatus.ACTIVE))
        );

        assertThatThrownBy(() -> adminUserService.getUserSummary(ADMIN_ID))
            .isInstanceOf(AtchagongException.class);
        verify(userRepository, never()).countByUserStatusInAndDeletedAtIsNull(org.mockito.ArgumentMatchers.anyCollection());
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 사용자 요약을 조회할 수 없다")
    void getUserSummaryWithUnknownUserThrowsException() {
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.getUserSummary(ADMIN_ID))
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    @DisplayName("관리자는 탈퇴하지 않은 사용자 목록을 조회할 수 있다")
    void getUsersReturnsNonWithdrawnUsers() {
        User admin = user(ADMIN_PK, ADMIN_ID, UserRole.ADMIN, UserStatus.ACTIVE);
        User targetUser = user(USER_PK, USER_ID, UserRole.USER, UserStatus.ACTIVE);
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin));
        given(userRepository.findAllByDeletedAtIsNull()).willReturn(List.of(targetUser));

        AdminUsersResponse response = adminUserService.getUsers(ADMIN_ID);

        assertThat(response.users()).hasSize(1);
        assertThat(response.users().get(0).userId()).isEqualTo(USER_ID);
        verify(userRepository).findAllByDeletedAtIsNull();
    }

    @Test
    @DisplayName("관리자는 사용자 상세에서 집중 통계와 연속 집중 일수를 조회할 수 있다")
    void getUserReturnsFocusStatistics() {
        User admin = user(ADMIN_PK, ADMIN_ID, UserRole.ADMIN, UserStatus.ACTIVE);
        User targetUser = user(USER_PK, USER_ID, UserRole.USER, UserStatus.ACTIVE);
        LocalDate today = LocalDate.now();
        List<FocusRecord> records = List.of(
            focusRecord(USER_PK, today.minusDays(2)),
            focusRecord(USER_PK, today.minusDays(1)),
            focusRecord(USER_PK, today)
        );
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin));
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(targetUser));
        given(focusRecordRepository.findAllByUserId(USER_PK)).willReturn(records);
        given(userBeverageRepository.findAllByUserId(USER_PK)).willReturn(List.of());
        given(selectedBeverageRepository.findByUserId(USER_PK)).willReturn(Optional.empty());

        AdminUserResponse response = adminUserService.getUser(ADMIN_ID, USER_ID);

        assertThat(response.totalFocusedSeconds()).isEqualTo(4_500L);
        assertThat(response.completedCupCount()).isEqualTo(3);
        assertThat(response.currentStreakDays()).isEqualTo(3);
        assertThat(response.selectedBeverageId()).isNull();
        assertThat(response.ownedBeverageCount()).isZero();
    }

    @Test
    @DisplayName("관리자는 일반 사용자의 상태를 변경할 수 있다")
    void updateStatusChangesRegularUserStatus() {
        User admin = user(ADMIN_PK, ADMIN_ID, UserRole.ADMIN, UserStatus.ACTIVE);
        User targetUser = user(USER_PK, USER_ID, UserRole.USER, UserStatus.ACTIVE);
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin));
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(targetUser));

        AdminUpdateStatusResponse response = adminUserService.updateStatus(
            ADMIN_ID,
            USER_ID,
            new AdminUpdateUserStatusRequest(UserStatus.SUSPENDED)
        );

        assertThat(targetUser.getUserStatus()).isEqualTo(UserStatus.SUSPENDED);
        assertThat(response.message()).isEqualTo("success");
        verify(userRepository).save(targetUser);
    }

    @Test
    @DisplayName("관리자는 자신 또는 다른 관리자 계정의 상태를 변경할 수 없다")
    void updateStatusRejectsAdminAccount() {
        User admin = user(ADMIN_PK, ADMIN_ID, UserRole.ADMIN, UserStatus.ACTIVE);
        User anotherAdmin = user(USER_PK, USER_ID, UserRole.ADMIN, UserStatus.ACTIVE);
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin));
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(anotherAdmin));

        assertThatThrownBy(() -> adminUserService.updateStatus(
            ADMIN_ID,
            USER_ID,
            new AdminUpdateUserStatusRequest(UserStatus.SUSPENDED)
        )).isInstanceOf(AtchagongException.class);

        verify(userRepository, never()).save(anotherAdmin);
    }

    @Test
    @DisplayName("관리자는 사용자의 코인 거래 내역을 최신순 페이지로 조회할 수 있다")
    void getCoinTransactionsReturnsTransactionPage() {
        User admin = user(ADMIN_PK, ADMIN_ID, UserRole.ADMIN, UserStatus.ACTIVE);
        User targetUser = user(USER_PK, USER_ID, UserRole.USER, UserStatus.ACTIVE);
        CoinTransaction transaction = CoinTransaction.create(
            targetUser,
            -100L,
            CoinTransactionType.BEVERAGE_PURCHASE,
            CoinReferenceType.BEVERAGE,
            3L,
            200L
        );
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin));
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(targetUser));
        given(coinTransactionRepository.findByUserId(
            org.mockito.ArgumentMatchers.eq(USER_PK),
            org.mockito.ArgumentMatchers.any(PageRequest.class)
        )).willReturn(new PageImpl<>(List.of(transaction), PageRequest.of(0, 20), 1));

        AdminCoinTransactionPageResponse response = adminUserService.getCoinTransactions(
            ADMIN_ID,
            USER_ID,
            new AdminCoinTransactionPageRequest(null, null)
        );

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().amount()).isEqualTo(-100L);
        assertThat(response.content().getFirst().balanceAfter()).isEqualTo(200L);
        assertThat(response.content().getFirst().transactionType())
            .isEqualTo(CoinTransactionType.BEVERAGE_PURCHASE);
        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(20);
    }

    private FocusRecord focusRecord(long userId, LocalDate date) {
        LocalDateTime completedAt = date.atTime(9, 25);
        return FocusRecord.create(
            userId,
            null,
            25,
            FocusRecord.FIXED_BREAK_MINUTES,
            FocusRecord.FIXED_CYCLE_COUNT,
            1_500,
            completedAt.minusMinutes(25),
            completedAt
        );
    }

    private User user(long id, String userId, UserRole role, UserStatus status) {
        return User.builder()
            .id(id)
            .userId(userId)
            .nickname("admin")
            .userRole(role)
            .userStatus(status)
            .onboardingCompleted(true)
            .build();
    }
}
