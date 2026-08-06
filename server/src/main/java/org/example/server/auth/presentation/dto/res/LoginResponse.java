package org.example.server.auth.presentation.dto.res;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    boolean isOnboardingCompleted
) {



    public static LoginResponse of(
        String refreshToken,
        String accessToken,
        boolean isOnboardingCompleted
    ) {
        return new LoginResponse(refreshToken, accessToken, isOnboardingCompleted);
    }

}
