package org.example.server.coin.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.coin.domain.models.CoinTransaction;
import org.example.server.coin.domain.models.UserCoinBalance;
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
class CoinJpaRepositoryTest {

    @Autowired
    private UserCoinBalanceJpaRepository userCoinBalanceJpaRepository;

    @Autowired
    private CoinTransactionJpaRepository coinTransactionJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("사용자별 코인 지갑을 조회한다")
    void findCoinBalanceByUserId() {
        User user = persistUser("coin-balance-user");
        userCoinBalanceJpaRepository.saveAndFlush(UserCoinBalance.create(user));

        UserCoinBalance userCoinBalance = userCoinBalanceJpaRepository.findByUser_Id(user.getId()).orElseThrow();

        assertThat(userCoinBalance.getUser()).isSameAs(user);
        assertThat(userCoinBalance.getBalance()).isZero();
    }

    @Test
    @DisplayName("같은 원본 참조값의 코인 거래는 중복 저장할 수 없다")
    void saveDuplicateCoinTransactionThrowsException() {
        User user = persistUser("coin-transaction-user");
        CoinTransaction firstTransaction = transaction(user);
        coinTransactionJpaRepository.saveAndFlush(firstTransaction);

        assertThat(coinTransactionJpaRepository
            .existsByUser_IdAndTransactionTypeAndReferenceTypeAndReferenceId(
                user.getId(),
                CoinTransactionType.ATTENDANCE,
                CoinReferenceType.ATTENDANCE,
                1L
            ))
            .isTrue();

        assertThatThrownBy(() -> coinTransactionJpaRepository.saveAndFlush(transaction(user)))
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

    private CoinTransaction transaction(User user) {
        return CoinTransaction.create(
            user,
            10,
            CoinTransactionType.ATTENDANCE,
            CoinReferenceType.ATTENDANCE,
            1L,
            10
        );
    }
}
