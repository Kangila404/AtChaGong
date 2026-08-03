package org.example.server.auth.presentation.dto.res;

public record RefreshTokenResponse(
    String refreshToken,
    String accessToken
) {
    public static RefreshTokenResponse of(String refreshToken, String accessToken) {
        return new RefreshTokenResponse(refreshToken, accessToken);
    }
}
