package org.example.server.coin.application;

import lombok.RequiredArgsConstructor;
import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.coin.domain.models.CoinTransaction;
import org.example.server.coin.domain.models.UserCoinBalance;
import org.example.server.coin.domain.repository.CoinTransactionRepository;
import org.example.server.coin.domain.repository.UserCoinBalanceRepository;
import org.example.server.coin.exception.CoinErrorCode;
import org.example.server.coin.exception.CoinException;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoinService {

    private final UserCoinBalanceRepository userCoinBalanceRepository;
    private final CoinTransactionRepository coinTransactionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public long getBalance(String userId) {
        return getBalance(findActiveUser(userId));
    }

    @Transactional(readOnly = true)
    public long getBalance(User user) {
        return userCoinBalanceRepository.findByUserId(user.getId())
            .orElseThrow(() -> new CoinException(CoinErrorCode.COIN_BALANCE_NOT_FOUND))
            .getBalance();
    }

    @Transactional
    public long changeBalance(
        User user,
        long amount,
        CoinTransactionType transactionType,
        CoinReferenceType referenceType,
        Long referenceId
    ) {
        if (coinTransactionRepository.existsByUserIdAndTransactionTypeAndReferenceTypeAndReferenceId(
            user.getId(), transactionType, referenceType, referenceId
        )) {
            throw new CoinException(CoinErrorCode.DUPLICATE_COIN_TRANSACTION);
        }

        UserCoinBalance balance = userCoinBalanceRepository.findWithLockByUserId(user.getId())
            .orElseThrow(() -> new CoinException(CoinErrorCode.COIN_BALANCE_NOT_FOUND));
        if (amount < 0 && balance.getBalance() < -amount) {
            throw new CoinException(CoinErrorCode.INSUFFICIENT_COIN);
        }

        balance.change(amount);
        CoinTransaction transaction = CoinTransaction.create(
            user, amount, transactionType, referenceType, referenceId, balance.getBalance()
        );
        coinTransactionRepository.save(transaction);
        return balance.getBalance();
    }

    private User findActiveUser(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        if (user.getUserStatus() == UserStatus.SUSPENDED) {
            throw new UserException(UserErrorCode.SUSPENDED_USER);
        }
        if (user.getUserStatus() == UserStatus.WITHDRAWN) {
            throw new UserException(UserErrorCode.WITHDRAWN_USER);
        }
        return user;
    }
}
