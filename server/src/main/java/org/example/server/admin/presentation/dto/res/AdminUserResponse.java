package org.example.server.admin.presentation.dto.res;

import java.time.LocalDateTime;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;

public record AdminUserResponse(
    String userId,
    String nickname,
    UserStatus userStatus,
    UserRole userRole,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt,
    LocalDateTime deletedAt,
    Long totalFocusedSeconds,
    Integer completedCupCount,
    Integer currentStreakDays,
    Long selectedBeverageId,
    String selectedBeverageName,
    Integer ownedBeverageCount
) {
    public static AdminUserResponse of(
        User user,
        Long totalFocusedSeconds,
        Integer completedCupCount,
        Integer currentStreakDays,
        Long selectedBeverageId,
        String selectedBeverageName,
        Integer ownedBeverageCount
    ) {
        return new AdminUserResponse(
            user.getUserId(),
            user.getNickname(),
            user.getUserStatus(),
            user.getUserRole(),
            user.getLastLoginAt(),
            user.getCreatedAt(),
            user.getDeletedAt(),
            totalFocusedSeconds,
            completedCupCount,
            currentStreakDays,
            selectedBeverageId,
            selectedBeverageName,
            ownedBeverageCount
        );
    }
}