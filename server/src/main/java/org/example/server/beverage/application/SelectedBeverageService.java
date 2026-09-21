package org.example.server.beverage.application;

import lombok.RequiredArgsConstructor;
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
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SelectedBeverageService {

    private final SelectedBeverageRepository selectedBeverageRepository;
    private final UserBeverageRepository userBeverageRepository;
    private final BeverageRepository beverageRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public SelectedBeverageResponse getSelectedBeverage(String userId) {
        User user = findActiveUser(userId);
        return selectedBeverageRepository.findByUserId(user.getId())
            .map(SelectedBeverageResponse::from)
            .orElseThrow(() -> new BeverageException(BeverageErrorCode.BEVERAGE_NOT_FOUND));
    }

    @Transactional
    public SelectedBeverageResponse selectBeverage(String userId, SelectBeverageRequest request) {
        User user = findActiveUser(userId);
        Long beverageId = validateRequest(request);
        Beverage beverage = beverageRepository.findById(beverageId)
            .orElseThrow(() -> new BeverageException(BeverageErrorCode.BEVERAGE_NOT_FOUND));
        UserBeverage ownership = userBeverageRepository
            .findByUserIdAndBeverageId(user.getId(), beverage.getId())
            .orElseThrow(() -> new BeverageException(BeverageErrorCode.BEVERAGE_NOT_OWNED));

        SelectedBeverage selectedBeverage = selectedBeverageRepository.findByUserId(user.getId())
            .map(savedSelection -> {
                savedSelection.select(ownership);
                return savedSelection;
            })
            .orElseGet(() -> selectedBeverageRepository.save(
                SelectedBeverage.create(user, ownership)
            ));
        return SelectedBeverageResponse.from(selectedBeverage);
    }

    @Transactional
    public void selectDefaultBeverage(User user, UserBeverage defaultOwnership) {
        SelectedBeverage selectedBeverage = selectedBeverageRepository.findByUserId(user.getId())
            .orElseGet(() -> SelectedBeverage.create(user, defaultOwnership));
        selectedBeverage.select(defaultOwnership);
        selectedBeverageRepository.save(selectedBeverage);
    }

    @Transactional
    public void clearSelection(User user) {
        selectedBeverageRepository.deleteByUserId(user.getId());
    }

    private Long validateRequest(SelectBeverageRequest request) {
        if (request == null || request.beverageId() == null) {
            throw new BeverageException(BeverageErrorCode.BEVERAGE_ID_REQUIRED);
        }
        return request.beverageId();
    }

    private User findActiveUser(String userId) {
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
