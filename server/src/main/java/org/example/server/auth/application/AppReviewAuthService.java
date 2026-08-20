package org.example.server.auth.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.domain.models.RefreshToken;
import org.example.server.auth.domain.repositories.RefreshTokenRepository;
import org.example.server.auth.exception.AuthErrorCode;
import org.example.server.auth.exception.AuthException;
import org.example.server.auth.infrastructure.jwt.JwtTokenProvider;
import org.example.server.auth.presentation.dto.req.AppReviewLoginRequest;
import org.example.server.auth.presentation.dto.res.LoginResponse;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppReviewAuthService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final String APP_REVIEW_LOGIN_ID = "app-review";
    private static final String APP_REVIEW_PASSWORD = "atchagong-review-2026";
    private static final String APP_REVIEW_USER_ID = "app-review";

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${spring.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public LoginResponse login(AppReviewLoginRequest request) {
        validateCredentials(request);

        User user = userRepository.findByUserId(APP_REVIEW_USER_ID)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        validateUserStatus(user.getUserStatus());
        user.updateLastLoginAt();

        String accessToken = jwtTokenProvider.createAccessToken(
            user.getUserId(),
            user.getUserRole().name()
        );
        String refreshToken = saveRefreshToken(user);

        return LoginResponse.of(accessToken, refreshToken, user.isOnboardingCompleted());
    }

    private void validateCredentials(AppReviewLoginRequest request) {
        if (!constantTimeEquals(request.loginId(), APP_REVIEW_LOGIN_ID)
            || !constantTimeEquals(request.password(), APP_REVIEW_PASSWORD)) {
            throw new AuthException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
        }
    }

    private void validateUserStatus(UserStatus userStatus) {
        if (userStatus != UserStatus.ACTIVE) {
            throw new UserException(UserErrorCode.SUSPENDED_USER);
        }
    }

    private String saveRefreshToken(User user) {
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());
        String hashedToken = hashToken(refreshToken);
        LocalDateTime expiredAt = LocalDateTime.now(KST)
            .plus(Duration.ofMillis(refreshTokenExpiration));

        RefreshToken savedToken = refreshTokenRepository.findByUserId(user.getId())
            .orElse(null);

        if (savedToken == null) {
            refreshTokenRepository.save(
                RefreshToken.builder()
                    .userId(user.getId())
                    .tokenHash(hashedToken)
                    .expiredAt(expiredAt)
                    .build()
            );
        } else {
            savedToken.updateToken(hashedToken, expiredAt);
        }

        return refreshToken;
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 not available", exception);
        }
    }

    private boolean constantTimeEquals(String left, String right) {
        if (left == null || right == null) {
            return false;
        }

        return MessageDigest.isEqual(
            left.getBytes(StandardCharsets.UTF_8),
            right.getBytes(StandardCharsets.UTF_8)
        );
    }
}
