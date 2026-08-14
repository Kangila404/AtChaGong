package org.example.server.user.presentation.dto.res;

import org.example.server.user.domain.models.User;

public record UpdateNicknameResponse(
    String userId,
    String nickname
) {
    public static UpdateNicknameResponse from(User user){
        return new UpdateNicknameResponse(user.getUserId(), user.getNickname());
    }
}
