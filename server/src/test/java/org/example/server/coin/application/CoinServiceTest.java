package org.example.server.coin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.coin.domain.models.UserCoinBalance;
import org.example.server.coin.domain.repository.CoinTransactionRepository;
import org.example.server.coin.domain.repository.UserCoinBalanceRepository;
import org.example.server.coin.exception.CoinException;
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
class CoinServiceTest {
    @InjectMocks private CoinService coinService;
    @Mock private UserCoinBalanceRepository balanceRepository;
    @Mock private CoinTransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;

    @Test
    void deductsBalanceAndWritesOneTransaction() {
        User user = user();
        UserCoinBalance balance = UserCoinBalance.create(user);
        balance.change(200);
        given(transactionRepository.existsByUserIdAndTransactionTypeAndReferenceTypeAndReferenceId(1L, CoinTransactionType.BEVERAGE_PURCHASE, CoinReferenceType.BEVERAGE, 3L)).willReturn(false);
        given(balanceRepository.findWithLockByUserId(1L)).willReturn(Optional.of(balance));

        long result = coinService.changeBalance(user, -100, CoinTransactionType.BEVERAGE_PURCHASE, CoinReferenceType.BEVERAGE, 3L);

        assertThat(result).isEqualTo(100);
        verify(transactionRepository).save(any());
    }

    @Test
    void rejectsInsufficientBalanceWithoutTransaction() {
        User user = user();
        UserCoinBalance balance = UserCoinBalance.create(user);
        given(transactionRepository.existsByUserIdAndTransactionTypeAndReferenceTypeAndReferenceId(1L, CoinTransactionType.BEVERAGE_PURCHASE, CoinReferenceType.BEVERAGE, 3L)).willReturn(false);
        given(balanceRepository.findWithLockByUserId(1L)).willReturn(Optional.of(balance));

        assertThatThrownBy(() -> coinService.changeBalance(user, -100, CoinTransactionType.BEVERAGE_PURCHASE, CoinReferenceType.BEVERAGE, 3L)).isInstanceOf(CoinException.class);
        verify(transactionRepository, never()).save(any());
        assertThat(balance.getBalance()).isZero();
    }

    @Test
    void rejectsDuplicateTransactionWithoutBalanceChange() {
        User user = user();
        UserCoinBalance balance = UserCoinBalance.create(user);
        given(transactionRepository.existsByUserIdAndTransactionTypeAndReferenceTypeAndReferenceId(1L, CoinTransactionType.ATTENDANCE, CoinReferenceType.ATTENDANCE, 1L)).willReturn(true);

        assertThatThrownBy(() -> coinService.changeBalance(user, 10, CoinTransactionType.ATTENDANCE, CoinReferenceType.ATTENDANCE, 1L)).isInstanceOf(CoinException.class);
        verify(balanceRepository, never()).findWithLockByUserId(any());
        assertThat(balance.getBalance()).isZero();
    }

    private User user() {
        return User.builder().id(1L).userId("user").nickname("tester").userStatus(UserStatus.ACTIVE).userRole(UserRole.USER).onboardingCompleted(true).build();
    }
}
