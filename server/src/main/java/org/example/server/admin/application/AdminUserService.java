package org.example.server.admin.application;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.server.admin.presentation.dto.req.AdminUpdateUserStatusRequest;
import org.example.server.admin.presentation.dto.res.AdminUpdateStatusResponse;
import org.example.server.admin.presentation.dto.res.AdminUserResponse;
import org.example.server.admin.presentation.dto.res.AdminUserSummaryResponse;
import org.example.server.admin.presentation.dto.res.AdminUsersResponse;
import org.example.server.beverage.domain.models.SelectedBeverage;
import org.example.server.beverage.domain.models.UserBeverage;
import org.example.server.beverage.domain.repository.SelectedBeverageRepository;
import org.example.server.beverage.domain.repository.UserBeverageRepository;
import org.example.server.common.exception.AtchagongException;
import org.example.server.common.exception.CommonErrorCode;
import org.example.server.record.domain.models.FocusRecord;
import org.example.server.record.domain.repository.FocusRecordRepository;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final UserRepository userRepository;
    private final FocusRecordRepository focusRecordRepository;
    private final UserBeverageRepository userBeverageRepository;
    private final SelectedBeverageRepository selectedBeverageRepository;

    @Transactional(readOnly = true)
    public AdminUserSummaryResponse getUserSummary(String userId) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);

        long totalUserCount = userRepository.countByUserStatusInAndDeletedAtIsNull(List.of(
            UserStatus.ACTIVE,
            UserStatus.SUSPENDED
        ));
        return new AdminUserSummaryResponse(totalUserCount);
    }
    @Transactional(readOnly = true)
    public AdminUsersResponse getUsers(String adminId){
        User admin = findUserByUserIdOrThrow(adminId);
        validateAdminUser(admin);
        List<User> users = userRepository.findAllByDeletedAtIsNull();
        return AdminUsersResponse.from(users);
    }

    @Transactional(readOnly = true)
    public AdminUserResponse getUser(String adminId, String userId) {
        User admin = findUserByUserIdOrThrow(adminId);
        validateAdminUser(admin);
        User user = findUserByUserIdOrThrow(userId);
        List<FocusRecord> focusRecords =
            focusRecordRepository.findAllByUserId(user.getId());

        long totalFocusedSeconds = focusRecords.stream()
            .mapToLong(FocusRecord::getFocusedSeconds)
            .sum();

        int completedCupCount = focusRecords.stream()
            .mapToInt(record -> record.getFocusedSeconds()
                / (record.getFocusMinutes() * 60))
            .sum();

        int currentStreakDays = calculateCurrentStreakDays(focusRecords);

        List<UserBeverage> userBeverages =
            userBeverageRepository.findAllByUserId(user.getId());

        int ownedBeverageCount = userBeverages.size();

        SelectedBeverage selectedBeverage =
            selectedBeverageRepository.findByUserId(user.getId())
                .orElse(null);

        Long selectedBeverageId = selectedBeverage == null
            ? null
            : selectedBeverage.getUserBeverage().getBeverage().getId();

        String selectedBeverageName = selectedBeverage == null
            ? null
            : selectedBeverage.getUserBeverage().getBeverage().getName();

        return AdminUserResponse.of(
            user,
            totalFocusedSeconds,
            completedCupCount,
            currentStreakDays,
            selectedBeverageId,
            selectedBeverageName,
            ownedBeverageCount
        );
    }

    @Transactional
    public AdminUpdateStatusResponse updateStatus(
        String adminId,
        String userId,
        AdminUpdateUserStatusRequest request){
        User admin = findUserByUserIdOrThrow(adminId);
        validateAdminUser(admin);
        User user = findUserByUserIdOrThrow(userId);

        if (admin.getId() == user.getId() || user.getUserRole() == UserRole.ADMIN) {
            throw new AtchagongException(CommonErrorCode.FORBIDDEN);
        }

        user.updateStatus(request.userStatus());
        userRepository.save(user);
        return new AdminUpdateStatusResponse("success");
    }

    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private void validateAdminUser(User user) {
        if (user.getUserStatus() != UserStatus.ACTIVE || user.getUserRole() != UserRole.ADMIN) {
            throw new AtchagongException(CommonErrorCode.FORBIDDEN);
        }
    }

    private int calculateCurrentStreakDays(List<FocusRecord> focusRecords) {
        Set<LocalDate> focusedDates = focusRecords.stream()
            .map(FocusRecord::getFocusedDate)
            .collect(Collectors.toSet());

        int streakDays = 0;
        LocalDate date = LocalDate.now(SEOUL_ZONE);

        if (!focusedDates.contains(date)) {
            date = date.minusDays(1);
        }

        while (focusedDates.contains(date)) {
            streakDays++;
            date = date.minusDays(1);
        }

        return streakDays;
    }
}
