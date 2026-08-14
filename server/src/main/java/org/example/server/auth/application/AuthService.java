package org.example.server.auth.application;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.domain.models.AuthAccount;
import org.example.server.auth.domain.models.RefreshToken;
import org.example.server.auth.domain.repositories.AuthAccountRepository;
import org.example.server.auth.domain.repositories.RefreshTokenRepository;
import org.example.server.auth.exception.AuthErrorCode;
import org.example.server.auth.exception.AuthException;
import org.example.server.auth.infrastructure.jwt.JwtTokenProvider;
import org.example.server.auth.presentation.dto.SocialUserInfo;
import org.example.server.auth.presentation.dto.req.LoginRequest;
import org.example.server.auth.presentation.dto.req.RefreshTokenRequest;
import org.example.server.auth.presentation.dto.res.LoginResponse;
import org.example.server.auth.presentation.dto.res.LogoutResponse;
import org.example.server.auth.presentation.dto.res.RefreshTokenResponse;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.ProfileImg;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.ProfileImgRepository;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${spring.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    private static final Long DEFAULT_PROFILE_IMG_ID = 1L;
    // jwt
    private final JwtTokenProvider jwtTokenProvider;
    // 리포지토리
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AuthAccountRepository authAccountRepository;
    private final List<SocialAuthProvider> socialAuthProviders;
    private final ProfileImgRepository profileImgRepository;


    @Transactional
    public RefreshTokenResponse reissue(RefreshTokenRequest request){
        RefreshToken refreshToken = findRefreshTokenOrThrow(request.refreshToken());
        validateRefreshTokenExpired(refreshToken);

        User user = findUserByIdOrThrow(refreshToken.getUserId());
        validateUserStatus(user.getUserStatus());
        validateRefreshTokenRevoked(refreshToken);

        String issuedRefreshToken = saveRefreshToken(user.getUserId());
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getUserRole().name());
        return RefreshTokenResponse.of(issuedRefreshToken, accessToken);
    }

    @Transactional
    public LoginResponse socialLogin(LoginRequest request) {

        SocialAuthProvider provider = findProviderOrThrow(request.authType());
        SocialUserInfo socialUserInfo = provider.verify(request.credential());

        AuthAccount authAccount = findOrCreateAuthAccount(
            request.authType(),
            socialUserInfo.providerId()
        );

        User user = authAccount.getUser();

        validateUserStatus(user.getUserStatus());
        user.updateLastLoginAt();

        String accessToken = jwtTokenProvider.createAccessToken(
            user.getUserId(),
            user.getUserRole().name()
        );

        String refreshToken = saveRefreshToken(user.getUserId());

        return LoginResponse.of(
            accessToken,
            refreshToken,
            user.isOnboardingCompleted()
        );
    }

    @Transactional
    public LogoutResponse logout(String userId){
        User user = findUserByUserIdOrThrow(userId);
        refreshTokenRepository.deleteByUserId(user.getId());
        return new LogoutResponse("success");
    }



    // ============= 메서드 모음 ============= //

    // 1. (Long)id -> User 조회
    private User findUserByIdOrThrow(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    // 2. (string)id -> User 조회
    private User findUserByUserIdOrThrow(String userId){
        return userRepository.findByUserId(userId)
            .orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));
        }
    // 3. AuthAccount 생성
    private AuthAccount findOrCreateAuthAccount(
        AuthType authType,
        String providerId
    ) {
        return authAccountRepository
            .findByProviderAndProviderId(authType, providerId)
            .orElseGet(() -> createSocialAccount(authType, providerId));
    }

    // 4. 소셜 계정  생성 메서드
    private AuthAccount createSocialAccount(AuthType authType, String providerId) {
        ProfileImg defaultProfileImg = profileImgRepository.findById(DEFAULT_PROFILE_IMG_ID)
            .orElseThrow(() -> new UserException(UserErrorCode.PROFILE_NOT_FOUND));

        User user = userRepository.save(
            User.createSocialUser(defaultProfileImg)
        );

        AuthAccount authAccount = AuthAccount.create(user, authType, providerId);
        return authAccountRepository.save(authAccount);
    }


    // ============= 검증 메서드 모음 ============= //

    // 1. 유저 상태 검증
    private void validateUserStatus(UserStatus userStatus){
        switch (userStatus) {
            case WITHDRAWN -> throw new UserException(UserErrorCode.WITHDRAWN_USER);
            case SUSPENDED -> throw new UserException(UserErrorCode.SUSPENDED_USER);
            case ACTIVE -> { }
            default -> throw new UserException(UserErrorCode.WITHDRAWN_USER); // 알 수 없는 상태는 안전하게 거부
        }
    }

    // 2. 소셜 로그인 종류 검증
    private SocialAuthProvider findProviderOrThrow(AuthType authType) {
        return socialAuthProviders.stream()
            .filter(provider -> provider.supports() == authType)
            .findFirst()
            .orElseThrow(() -> new AuthException(AuthErrorCode.UNSUPPORTED_PROVIDER));
    }

    // 3. 리프레시 토큰 폐기 여부 확인
    private void validateRefreshTokenRevoked(RefreshToken refreshToken){
        if(refreshToken.isRevoked()){
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_REVOKED);
        }
    }

    // ============= JWT 메서드 모음 ============= //
    // 1. 리프레시 토큰 기한 검증
    private void validateRefreshTokenExpired(RefreshToken refreshToken){
        if(refreshToken.isExpired()){
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        }
    }

    // 2. tokenHash -> refreshToken 조회
    private RefreshToken findRefreshTokenOrThrow(String hashToken){
        return refreshTokenRepository.findByTokenHash(hashToken)
            .orElseThrow(()-> new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN));
    }

    // 3. 리프레시 토큰 발급 및 저장
    private String saveRefreshToken(String userId) {
        User user = findUserByUserIdOrThrow(userId);

        String refreshToken =
            jwtTokenProvider.createRefreshToken(userId);
        String hashedToken = hashToken(refreshToken);

        RefreshToken savedToken =
            refreshTokenRepository.findByUserId(user.getId())
                .orElse(null);

        LocalDateTime expiredAt = LocalDateTime.now(ZoneId.of("Asia/Seoul")).plus(Duration.ofMillis(refreshTokenExpiration));

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
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }



}
