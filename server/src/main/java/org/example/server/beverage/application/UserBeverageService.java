package org.example.server.beverage.application;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.enums.BeverageAcquisitionType;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.models.UserBeverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.domain.repository.UserBeverageRepository;
import org.example.server.beverage.exception.BeverageErrorCode;
import org.example.server.beverage.exception.BeverageException;
import org.example.server.beverage.presentation.dto.res.UserBeverageResponse;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBeverageService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final UserBeverageRepository userBeverageRepository;
    private final BeverageRepository beverageRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserBeverageResponse> getUserBeverages(String userId) {
        User user = findActiveUserByUserId(userId);
        return userBeverageRepository.findAllByUserId(user.getId()).stream()
            .map(UserBeverageResponse::from)
            .toList();
    }

    @Transactional
    public void grantDefaultBeverage(User user) {
        Beverage defaultBeverage = beverageRepository.findDefault()
            .orElseThrow(() -> new BeverageException(BeverageErrorCode.BEVERAGE_NOT_FOUND));
        if (userBeverageRepository.existsByUserIdAndBeverageId(user.getId(), defaultBeverage.getId())) {
            return;
        }

        userBeverageRepository.save(UserBeverage.create(
            user,
            defaultBeverage,
            BeverageAcquisitionType.DEFAULT,
            LocalDateTime.now(SEOUL_ZONE)
        ));
    }

    @Transactional
    public void resetToDefaultBeverage(User user) {
        userBeverageRepository.deleteByUserId(user.getId());
        grantDefaultBeverage(user);
    }

    private User findActiveUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new UserException(user.getUserStatus() == UserStatus.SUSPENDED
                ? UserErrorCode.SUSPENDED_USER
                : UserErrorCode.WITHDRAWN_USER);
        }
        return user;
    }
}
