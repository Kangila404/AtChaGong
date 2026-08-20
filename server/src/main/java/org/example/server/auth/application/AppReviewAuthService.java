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
import org.example.server.user.domain.models.ProfileImg;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.ProfileImgRepository;
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
    private static final Long DEFAULT_PROFILE_IMG_ID = 1L;

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final ProfileImgRepository profileImgRepository;

    @Value("${spring.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${app-review.login-id:app-review}")
    private String appReviewLoginId;

    @Value("${app-review.password:}")
    private String appReviewPassword;

    @Value("${app-review.user-id:app-review}")
    private String appReviewUserId;

    @Transactional
    public LoginResponse login(AppReviewLoginRequest request) {
        validateCredentials(request);

        User user = userRepository.findByUserId(appReviewUserId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        restoreWithdrawnReviewUser(user);
        validateActiveUser(user.getUserStatus());
        user.completeOnboarding();
        user.updateLastLoginAt();

        String accessToken = jwtTokenProvider.createAccessToken(
            user.getUserId(),
            user.getUserRole().name()
        );
        String refreshToken = saveRefreshToken(user);

        return LoginResponse.of(accessToken, refreshToken, user.isOnboardingCompleted());
    }

    private void validateCredentials(AppReviewLoginRequest request) {
        if (appReviewPassword == null || appReviewPassword.isBlank()
            || !constantTimeEquals(request.loginId(), appReviewLoginId)
            || !constantTimeEquals(request.password(), appReviewPassword)) {
            throw new AuthException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
        }
    }

    private void restoreWithdrawnReviewUser(User user) {
        if (user.getUserStatus() != UserStatus.WITHDRAWN) {
            return;
        }

        ProfileImg defaultProfileImg = profileImgRepository.findById(DEFAULT_PROFILE_IMG_ID)
            .orElseThrow(() -> new UserException(UserErrorCode.PROFILE_NOT_FOUND));
        user.reactivateForRejoin(defaultProfileImg);
    }

    private void validateActiveUser(UserStatus userStatus) {
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
