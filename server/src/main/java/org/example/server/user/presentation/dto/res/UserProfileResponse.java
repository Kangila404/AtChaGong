package org.example.server.user.presentation.dto.res;

import org.example.server.user.domain.models.User;

public record UserProfileResponse(
    Long profileId,
    String profileImg,
    String name
) {
    public static  UserProfileResponse from(User user){
        return new  UserProfileResponse(
            user.getProfileImg().getId(),
            user.getProfileImg().getImgUrl(),
            user.getProfileImg().getName()
        );
    }
}
