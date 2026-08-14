package org.example.server.user.presentation.dto.res;

import java.time.LocalDateTime;
import org.example.server.user.domain.models.User;

public record UserMeResponse(
    String nickname,
    String userStatus,
    String userRole,
    boolean onboardingCompleted,
    LocalDateTime lastLoginAt
) {


    public static UserMeResponse from(User user) {
        return new UserMeResponse(
            user.getNickname(),
            user.getUserStatus().name(),
            user.getUserRole().name(),
            user.isOnboardingCompleted(),
            user.getLastLoginAt()
        );
    }

}
