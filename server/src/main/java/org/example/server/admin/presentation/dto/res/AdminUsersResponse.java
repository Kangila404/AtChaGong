package org.example.server.admin.presentation.dto.res;

import java.time.LocalDateTime;
import java.util.List;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;

public record AdminUsersResponse(
    List<UserInfo> users
) {
    public static AdminUsersResponse from(List<User> users) {
        List<UserInfo> userInfos = users.stream()
            .map(UserInfo::from)
            .toList();

        return new AdminUsersResponse(userInfos);
    }

    public record UserInfo(
        String userId,
        String nickname,
        UserStatus userStatus,
        UserRole userRole,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
    ) {
        public static UserInfo from(User user) {
            return new UserInfo(
                user.getUserId(),
                user.getNickname(),
                user.getUserStatus(),
                user.getUserRole(),
                user.getLastLoginAt(),
                user.getCreatedAt()
            );
        }
    }
}