package org.example.server.user.presentation.dto.res;

import org.example.server.user.domain.models.ProfileImg;

public record ProfileImgResponse(
    Long profileId,
    String imgUrl
) {
    public static ProfileImgResponse from(ProfileImg profileImg) {
        return new ProfileImgResponse(
            profileImg.getId(),
            profileImg.getImgUrl()
        );
    }
}
