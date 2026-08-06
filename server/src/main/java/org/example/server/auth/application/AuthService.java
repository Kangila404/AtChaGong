package org.example.server.auth.application;

import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.domain.models.AuthAccount;
import org.example.server.auth.domain.models.RefreshToken;
import org.example.server.auth.domain.repositories.AuthAccountRepository;
import org.example.server.auth.domain.repositories.RefreshTokenRepository;
import org.example.server.auth.infrastructure.jwt.JwtTokenProvider;
import org.example.server.auth.presentation.dto.SocialUserInfo;
import org.example.server.auth.presentation.dto.req.LoginRequest;
import org.example.server.auth.presentation.dto.req.RefreshTokenRequest;
import org.example.server.auth.presentation.dto.res.LoginResponse;
import org.example.server.auth.presentation.dto.res.LogoutResponse;
import org.example.server.auth.presentation.dto.res.RefreshTokenResponse;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    // jwt
    private final JwtTokenProvider jwtTokenProvider;
    // 리포지토리
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AuthAccountRepository authAccountRepository;
    private final SocialAuthProvider socialAuthProvider;


    @Transactional
    public RefreshTokenResponse reissue(RefreshTokenRequest request){
        RefreshToken refreshToken = findRefreshTokenOrThrow(request.refreshToken());
        validateRefreshTokenExpired(refreshToken);

        User user = findUserByIdOrThrow(refreshToken.getUserId());
        validateUserStatus(user.getUserStatus());

        String issuedRefreshToken = saveRefreshToken(user.getUserId());
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getUserRole().name());
        return RefreshTokenResponse.of(issuedRefreshToken, accessToken);
    }

    @Transactional
    public LoginResponse socialLogin(LoginRequest request) {

        validateSupportedProvider(request.authType());

        // 소셜 credential 검증 후 해당 소셜 계정의 고유 ID 추출
        SocialUserInfo socialUserInfo =
            socialAuthProvider.verify(request.credential());

        // 기존 계정 조회, 처음 로그인한 계정이면 자동 등록
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
            .orElseThrow(()-> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    // 2. (string)id -> User 조회
    private User findUserByUserIdOrThrow(String userId){
        return userRepository.findByUserId(userId)
            .orElseThrow(()-> new IllegalArgumentException("유저를 찾을 수 없습니다."));
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
        User user = userRepository.save(
            User.createSocialUser()
        );

        AuthAccount authAccount = AuthAccount.create(user, authType, providerId);

        return authAccountRepository.save(authAccount);
    }


    // ============= 검증 메서드 모음 ============= //

    // 1. 유저 상태 검증
    private void validateUserStatus(UserStatus userStatus){
        if(!userStatus.equals(UserStatus.ACTIVE)){
            throw new IllegalArgumentException("비활성화된 유저입니다.");
        }
    }

    // 2. 소셜 로그인 종류 검증
    private void validateSupportedProvider(AuthType authType) {
        if (socialAuthProvider.supports() != authType) {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
        }
    }

    // ============= JWT 메서드 모음 ============= //
    // 1. 리프레시 토큰 기한 검증
    private void validateRefreshTokenExpired(RefreshToken refreshToken){
        if(refreshToken.isExpired()){
            throw new IllegalArgumentException(("만료된 토큰입니다."));
        }
    }

    // 2. tokenHash -> refreshToken 조회
    private RefreshToken findRefreshTokenOrThrow(String hashToken){
        return refreshTokenRepository.findByTokenHash(hashToken)
            .orElseThrow(()-> new IllegalArgumentException("토큰을 찾을 수 없습니다."));
    }

    // 3. 리프레시 토큰 발급 및 저장
    private String saveRefreshToken(String userId){
        User user = findUserByUserIdOrThrow(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        RefreshToken savedToken = refreshTokenRepository.findByUserId(user.getId()).orElse(null);

        if(savedToken == null){
            refreshTokenRepository.save(
                RefreshToken.builder()
                    .userId(user.getId())
                    .tokenHash(refreshToken)
                    .expiredAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(7))
                    .build());
        } else {
            savedToken.updateToken(refreshToken, LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(7));
        }
        return refreshToken;
    }



}
