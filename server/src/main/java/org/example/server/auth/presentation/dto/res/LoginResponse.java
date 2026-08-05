package org.example.server.auth.presentation.dto.res;

public record LoginResponse(
    String accessToken,
    String refreshToken
) {



    public static LoginResponse of(
        String refreshToken,
        String accessToken
    ) {
        return new LoginResponse(refreshToken, accessToken);
    }

}
