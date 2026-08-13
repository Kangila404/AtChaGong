package org.example.server.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.example.server.user.presentation.dto.req.UpdateNicknameRequest;
import org.example.server.user.presentation.dto.res.UpdateNicknameResponse;
import org.example.server.user.presentation.dto.res.UserMeResponse;
import org.example.server.user.presentation.dto.res.WithdrawUserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String USER_ID = "user-1";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("활성 유저는 내 정보를 조회할 수 있다")
    void getMeReturnsActiveUser() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user(UserStatus.ACTIVE)));

        UserMeResponse response = userService.getMe(USER_ID);

        assertThat(response.nickname()).isEqualTo("tester");
        assertThat(response.userStatus()).isEqualTo(UserStatus.ACTIVE.name());
        assertThat(response.userRole()).isEqualTo(UserRole.USER.name());
        assertThat(response.onboardingCompleted()).isTrue();
    }

    @Test
    @DisplayName("정지 유저는 내 정보를 조회할 수 없다")
    void getMeWithSuspendedUserThrowsException() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user(UserStatus.SUSPENDED)));

        assertThatThrownBy(() -> userService.getMe(USER_ID))
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.SUSPENDED_USER.name());
    }

    @Test
    @DisplayName("닉네임을 변경하면 응답에 변경된 닉네임을 반환한다")
    void updateNicknameChangesNickname() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user(UserStatus.ACTIVE)));

        UpdateNicknameResponse response = userService.updateNickname(USER_ID, new UpdateNicknameRequest("new-name"));

        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.nickname()).isEqualTo("new-name");
    }

    @Test
    @DisplayName("회원 탈퇴를 하면 유저 상태가 탈퇴로 변경된다")
    void withdrawChangesUserStatus() {
        User user = user(UserStatus.ACTIVE);
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user));

        WithdrawUserResponse response = userService.withdraw(USER_ID);

        assertThat(response.message()).isEqualTo("success");
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.WITHDRAWN);
        assertThat(user.getDeletedAt()).isNotNull();
    }

    private User user(UserStatus status) {
        return User.builder()
            .id(1L)
            .userId(USER_ID)
            .nickname("tester")
            .userStatus(status)
            .userRole(UserRole.USER)
            .onboardingCompleted(true)
            .build();
    }
}
