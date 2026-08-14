package org.example.server.user.presentation.dto.res;

import org.example.server.user.domain.models.User;

public record UpdateProfileImgResponse(
    Long profileId,
    String imgUrl
) {
    public static  UpdateProfileImgResponse from(User user){
        return new  UpdateProfileImgResponse(
            user.getProfileImg().getId(),
            user.getProfileImg().getImgUrl()
        );
    }
}
