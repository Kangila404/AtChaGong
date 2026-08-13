package org.example.server.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String USER_ID = "user-1";
    private static final long USER_PK = 1L;

    private AuthService authService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthAccountRepository authAccountRepository;

    @Mock
    private SocialAuthProvider socialAuthProvider;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
            jwtTokenProvider,
            refreshTokenRepository,
            userRepository,
            authAccountRepository,
            List.of(socialAuthProvider)
        );
    }

    @Test
    @DisplayName("기존 소셜 계정으로 로그인하면 토큰을 발급하고 마지막 로그인 시간을 갱신한다")
    void socialLoginWithExistingAccountIssuesTokens() {
        User user = user(UserStatus.ACTIVE);
        AuthAccount authAccount = AuthAccount.create(user, AuthType.KAKAO, "provider-1");
        given(socialAuthProvider.supports()).willReturn(AuthType.KAKAO);
        given(socialAuthProvider.verify("credential")).willReturn(new SocialUserInfo("provider-1"));
        given(authAccountRepository.findByProviderAndProviderId(AuthType.KAKAO, "provider-1"))
            .willReturn(Optional.of(authAccount));
        given(jwtTokenProvider.createAccessToken(USER_ID, UserRole.USER.name())).willReturn("access-token");
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user));
        given(jwtTokenProvider.createRefreshToken(USER_ID)).willReturn("refresh-token");
        given(refreshTokenRepository.findByUserId(USER_PK)).willReturn(Optional.empty());

        LoginResponse response = authService.socialLogin(new LoginRequest(AuthType.KAKAO, "credential"));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.isOnboardingCompleted()).isTrue();
        assertThat(user.getLastLoginAt()).isNotNull();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("지원하지 않는 소셜 제공자는 로그인할 수 없다")
    void socialLoginWithUnsupportedProviderThrowsException() {
        given(socialAuthProvider.supports()).willReturn(AuthType.GOOGLE);

        assertThatThrownBy(() -> authService.socialLogin(new LoginRequest(AuthType.KAKAO, "credential")))
            .isInstanceOf(AuthException.class)
            .extracting("code")
            .isEqualTo(AuthErrorCode.UNSUPPORTED_PROVIDER.name());
        verify(authAccountRepository, never()).findByProviderAndProviderId(any(), any());
    }

    @Test
    @DisplayName("정지된 사용자는 소셜 로그인할 수 없다")
    void socialLoginWithSuspendedUserThrowsException() {
        User suspendedUser = user(UserStatus.SUSPENDED);
        given(socialAuthProvider.supports()).willReturn(AuthType.KAKAO);
        given(socialAuthProvider.verify("credential")).willReturn(new SocialUserInfo("provider-1"));
        given(authAccountRepository.findByProviderAndProviderId(AuthType.KAKAO, "provider-1"))
            .willReturn(Optional.of(AuthAccount.create(suspendedUser, AuthType.KAKAO, "provider-1")));

        assertThatThrownBy(() -> authService.socialLogin(new LoginRequest(AuthType.KAKAO, "credential")))
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.SUSPENDED_USER.name());
        verify(jwtTokenProvider, never()).createAccessToken(any(), any());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("탈퇴한 사용자는 소셜 로그인할 수 없다")
    void socialLoginWithWithdrawnUserThrowsException() {
        User withdrawnUser = user(UserStatus.WITHDRAWN);
        given(socialAuthProvider.supports()).willReturn(AuthType.KAKAO);
        given(socialAuthProvider.verify("credential")).willReturn(new SocialUserInfo("provider-1"));
        given(authAccountRepository.findByProviderAndProviderId(AuthType.KAKAO, "provider-1"))
            .willReturn(Optional.of(AuthAccount.create(withdrawnUser, AuthType.KAKAO, "provider-1")));

        assertThatThrownBy(() -> authService.socialLogin(new LoginRequest(AuthType.KAKAO, "credential")))
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.WITHDRAWN_USER.name());
        verify(jwtTokenProvider, never()).createAccessToken(any(), any());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("유효한 리프레시 토큰이면 새 토큰 쌍을 발급한다")
    void reissueWithValidRefreshTokenIssuesNewTokens() {
        User user = user(UserStatus.ACTIVE);
        RefreshToken savedToken = refreshToken("old-refresh-token", LocalDateTime.now().plusDays(1), null);
        given(refreshTokenRepository.findByTokenHash("old-refresh-token")).willReturn(Optional.of(savedToken));
        given(userRepository.findById(USER_PK)).willReturn(Optional.of(user));
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user));
        given(jwtTokenProvider.createRefreshToken(USER_ID)).willReturn("new-refresh-token");
        given(refreshTokenRepository.findByUserId(USER_PK)).willReturn(Optional.of(savedToken));
        given(jwtTokenProvider.createAccessToken(USER_ID, UserRole.USER.name())).willReturn("new-access-token");

        RefreshTokenResponse response = authService.reissue(new RefreshTokenRequest("old-refresh-token"));

        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(savedToken.getTokenHash()).isEqualTo("new-refresh-token");
        assertThat(savedToken.isRevoked()).isFalse();
    }

    @Test
    @DisplayName("폐기된 리프레시 토큰은 재발급할 수 없다")
    void reissueWithRevokedRefreshTokenThrowsException() {
        User user = user(UserStatus.ACTIVE);
        RefreshToken revokedToken = refreshToken(
            "refresh-token",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now()
        );
        given(refreshTokenRepository.findByTokenHash("refresh-token")).willReturn(Optional.of(revokedToken));
        given(userRepository.findById(USER_PK)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.reissue(new RefreshTokenRequest("refresh-token")))
            .isInstanceOf(AuthException.class)
            .extracting("code")
            .isEqualTo(AuthErrorCode.REFRESH_TOKEN_REVOKED.name());
        verify(jwtTokenProvider, never()).createAccessToken(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 리프레시 토큰은 재발급할 수 없다")
    void reissueWithUnknownRefreshTokenThrowsException() {
        given(refreshTokenRepository.findByTokenHash("unknown-token")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.reissue(new RefreshTokenRequest("unknown-token")))
            .isInstanceOf(AuthException.class)
            .extracting("code")
            .isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN.name());
        verify(userRepository, never()).findById(any());
        verify(jwtTokenProvider, never()).createAccessToken(any(), any());
    }

    @Test
    @DisplayName("만료된 리프레시 토큰은 재발급할 수 없다")
    void reissueWithExpiredRefreshTokenThrowsException() {
        RefreshToken expiredToken = refreshToken(
            "expired-token",
            LocalDateTime.now().minusSeconds(1),
            null
        );
        given(refreshTokenRepository.findByTokenHash("expired-token"))
            .willReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> authService.reissue(new RefreshTokenRequest("expired-token")))
            .isInstanceOf(AuthException.class)
            .extracting("code")
            .isEqualTo(AuthErrorCode.REFRESH_TOKEN_EXPIRED.name());
        verify(userRepository, never()).findById(any());
        verify(jwtTokenProvider, never()).createAccessToken(any(), any());
    }

    @Test
    @DisplayName("토큰에 연결된 사용자가 없으면 재발급할 수 없다")
    void reissueWithUnknownUserThrowsException() {
        RefreshToken savedToken = refreshToken(
            "refresh-token",
            LocalDateTime.now().plusDays(1),
            null
        );
        given(refreshTokenRepository.findByTokenHash("refresh-token"))
            .willReturn(Optional.of(savedToken));
        given(userRepository.findById(USER_PK)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.reissue(new RefreshTokenRequest("refresh-token")))
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.USER_NOT_FOUND.name());
        verify(jwtTokenProvider, never()).createAccessToken(any(), any());
    }

    @Test
    @DisplayName("정지된 사용자는 리프레시 토큰을 재발급할 수 없다")
    void reissueWithSuspendedUserThrowsException() {
        RefreshToken savedToken = refreshToken(
            "refresh-token",
            LocalDateTime.now().plusDays(1),
            null
        );
        given(refreshTokenRepository.findByTokenHash("refresh-token"))
            .willReturn(Optional.of(savedToken));
        given(userRepository.findById(USER_PK)).willReturn(Optional.of(user(UserStatus.SUSPENDED)));

        assertThatThrownBy(() -> authService.reissue(new RefreshTokenRequest("refresh-token")))
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.SUSPENDED_USER.name());
        verify(jwtTokenProvider, never()).createAccessToken(any(), any());
        verify(jwtTokenProvider, never()).createRefreshToken(any());
    }

    @Test
    @DisplayName("탈퇴한 사용자는 리프레시 토큰을 재발급할 수 없다")
    void reissueWithWithdrawnUserThrowsException() {
        RefreshToken savedToken = refreshToken(
            "refresh-token",
            LocalDateTime.now().plusDays(1),
            null
        );
        given(refreshTokenRepository.findByTokenHash("refresh-token"))
            .willReturn(Optional.of(savedToken));
        given(userRepository.findById(USER_PK)).willReturn(Optional.of(user(UserStatus.WITHDRAWN)));

        assertThatThrownBy(() -> authService.reissue(new RefreshTokenRequest("refresh-token")))
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.WITHDRAWN_USER.name());
        verify(jwtTokenProvider, never()).createAccessToken(any(), any());
        verify(jwtTokenProvider, never()).createRefreshToken(any());
    }

    @Test
    @DisplayName("로그아웃하면 유저의 리프레시 토큰을 삭제한다")
    void logoutDeletesRefreshToken() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user(UserStatus.ACTIVE)));

        LogoutResponse response = authService.logout(USER_ID);

        assertThat(response.message()).isEqualTo("success");
        verify(refreshTokenRepository).deleteByUserId(USER_PK);
    }

    @Test
    @DisplayName("존재하지 않는 유저는 로그아웃할 수 없다")
    void logoutWithUnknownUserThrowsException() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logout(USER_ID))
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.USER_NOT_FOUND.name());
        verify(refreshTokenRepository, never()).deleteByUserId(any());
    }

    private User user(UserStatus status) {
        return User.builder()
            .id(USER_PK)
            .userId(USER_ID)
            .nickname("tester")
            .userStatus(status)
            .userRole(UserRole.USER)
            .onboardingCompleted(true)
            .build();
    }

    private RefreshToken refreshToken(String tokenHash, LocalDateTime expiredAt, LocalDateTime revokedAt) {
        return RefreshToken.builder()
            .userId(USER_PK)
            .tokenHash(tokenHash)
            .expiredAt(expiredAt)
            .revokedAt(revokedAt)
            .build();
    }
}
