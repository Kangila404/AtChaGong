package org.example.server.user.presentation.dto.res;

import java.time.LocalDateTime;
import org.example.server.user.domain.models.User;

public record UserMeResponse(
    String userId,
    String nickname,
    String userStatus,
    String userRole,
    boolean onboardingCompleted,
    LocalDateTime lastLoginAt
) {


//    public static of(User user){
//        UserMeResponse dto =  new UserMeResponse();
//        dto.message = user.getUserId();
//        return dto;
//    }

}
