package org.example.server.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.example.server.auth.domain.models.RefreshToken;
import org.example.server.auth.domain.repositories.RefreshTokenRepository;
import org.example.server.auth.infrastructure.jwt.JwtTokenProvider;
import org.example.server.auth.presentation.dto.req.DevLoginRequest;
import org.example.server.auth.presentation.dto.res.DevSignupResponse;
import org.example.server.auth.presentation.dto.res.LoginResponse;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.ProfileImg;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.ProfileImgRepository;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DevAuthServiceTest {

    private static final String USER_ID = "user-1";
    private static final long USER_PK = 1L;

    @InjectMocks
    private DevAuthService devAuthService;

    @Mock
    private ProfileImgRepository profileImgRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("개발 회원가입은 기본 프로필 이미지로 사용자를 생성한다")
    void signupCreatesUserWithDefaultProfileImage() {
        ProfileImg profileImg = ProfileImg.builder()
            .id(1L)
            .name("Profile_1")
            .imgUrl("https://example.com/profile-1.png")
            .build();
        given(profileImgRepository.findById(1L)).willReturn(Optional.of(profileImg));
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        DevSignupResponse response = devAuthService.signup();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertThat(response.userId()).isEqualTo(savedUser.getUserId());
        assertThat(savedUser.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(savedUser.getUserRole()).isEqualTo(UserRole.USER);
        assertThat(savedUser.isOnboardingCompleted()).isFalse();
        assertThat(savedUser.getProfileImg()).isSameAs(profileImg);
    }

    @Test
    @DisplayName("기본 프로필 이미지가 없으면 개발 회원가입을 할 수 없다")
    void signupWithoutDefaultProfileImageThrowsException() {
        given(profileImgRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> devAuthService.signup())
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.PROFILE_NOT_FOUND.name());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("개발 로그인은 토큰을 발급하고 리프레시 토큰을 저장한다")
    void loginIssuesTokensAndSavesRefreshToken() {
        User user = activeUser();
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user));
        given(jwtTokenProvider.createRefreshToken(USER_ID)).willReturn("refresh-token");
        given(refreshTokenRepository.findByUserId(USER_PK)).willReturn(Optional.empty());
        given(jwtTokenProvider.createAccessToken(USER_ID, UserRole.USER.name())).willReturn("access-token");

        LoginResponse response = devAuthService.login(new DevLoginRequest(USER_ID));

        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.isOnboardingCompleted()).isTrue();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("비활성 사용자는 개발 로그인할 수 없다")
    void loginWithInactiveUserThrowsException() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user(UserStatus.SUSPENDED)));

        assertThatThrownBy(() -> devAuthService.login(new DevLoginRequest(USER_ID)))
            .isInstanceOf(IllegalArgumentException.class);
        verify(jwtTokenProvider, never()).createAccessToken(any(), any());
    }

    private User activeUser() {
        return user(UserStatus.ACTIVE);
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
}
