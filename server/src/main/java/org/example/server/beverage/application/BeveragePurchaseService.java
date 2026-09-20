package org.example.server.beverage.application;

import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.enums.BeverageAcquisitionType;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.models.UserBeverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.domain.repository.UserBeverageRepository;
import org.example.server.beverage.exception.BeverageErrorCode;
import org.example.server.beverage.exception.BeverageException;
import org.example.server.beverage.presentation.dto.res.BeveragePurchaseResponse;
import org.example.server.coin.application.CoinService;
import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BeveragePurchaseService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final BeverageRepository beverageRepository;
    private final UserBeverageRepository userBeverageRepository;
    private final UserRepository userRepository;
    private final CoinService coinService;

    @Transactional
    public BeveragePurchaseResponse purchase(String userId, Long beverageId) {
        User user = findActiveUser(userId);
        Beverage beverage = beverageRepository.findById(beverageId)
            .orElseThrow(() -> new BeverageException(BeverageErrorCode.BEVERAGE_NOT_FOUND));
        LocalDateTime now = LocalDateTime.now(SEOUL_ZONE);
        if (!beverage.isAvailableForSale(now)) {
            throw new BeverageException(BeverageErrorCode.BEVERAGE_NOT_ON_SALE);
        }
        if (userBeverageRepository.existsByUserIdAndBeverageId(user.getId(), beverageId)) {
            throw new BeverageException(BeverageErrorCode.BEVERAGE_ALREADY_OWNED);
        }

        long balance = coinService.changeBalance(
            user,
            -beverage.getPrice(),
            CoinTransactionType.BEVERAGE_PURCHASE,
            CoinReferenceType.BEVERAGE,
            beverageId
        );
        userBeverageRepository.save(UserBeverage.create(
            user, beverage, BeverageAcquisitionType.PURCHASE, now
        ));
        return new BeveragePurchaseResponse(beverageId, beverage.getName(), beverage.getPrice(), balance);
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
