package org.example.server.beverage.application;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.enums.BeverageAcquisitionType;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.models.UserBeverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.domain.repository.SelectedBeverageRepository;
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
    private final SelectedBeverageRepository selectedBeverageRepository;

    @Transactional(readOnly = true)
    public List<UserBeverageResponse> getUserBeverages(String userId) {
        User user = findActiveUserByUserId(userId);
        Long selectedUserBeverageId = selectedBeverageRepository.findByUserId(user.getId())
            .map(selectedBeverage -> selectedBeverage.getUserBeverage().getId())
            .orElse(null);
        return userBeverageRepository.findAllByUserId(user.getId()).stream()
            .map(userBeverage -> UserBeverageResponse.from(
                userBeverage,
                selectedUserBeverageId != null
                    && Objects.equals(userBeverage.getId(), selectedUserBeverageId)
            ))
            .toList();
    }

    @Transactional
    public UserBeverage grantDefaultBeverage(User user) {
        Beverage defaultBeverage = beverageRepository.findDefault()
            .orElseThrow(() -> new BeverageException(BeverageErrorCode.BEVERAGE_NOT_FOUND));
        return userBeverageRepository
            .findByUserIdAndBeverageId(user.getId(), defaultBeverage.getId())
            .orElseGet(() -> userBeverageRepository.save(UserBeverage.create(
                user,
                defaultBeverage,
                BeverageAcquisitionType.DEFAULT,
                LocalDateTime.now(SEOUL_ZONE)
            )));
    }

    @Transactional
    public UserBeverage resetToDefaultBeverage(User user) {
        userBeverageRepository.deleteByUserId(user.getId());
        return grantDefaultBeverage(user);
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
