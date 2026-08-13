package org.example.server.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.example.server.admin.presentation.dto.res.AdminUserSummaryResponse;
import org.example.server.common.exception.AtchagongException;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    private static final String ADMIN_ID = "admin-1";

    @InjectMocks
    private AdminUserService adminUserService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("관리자는 전체 사용자 요약을 조회할 수 있다")
    void getUserSummaryReturnsTotalUserCount() {
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(user(UserRole.ADMIN, UserStatus.ACTIVE)));
        given(userRepository.countByUserStatusInAndDeletedAtIsNull(org.mockito.ArgumentMatchers.anyCollection()))
            .willReturn(12L);

        AdminUserSummaryResponse response = adminUserService.getUserSummary(ADMIN_ID);

        assertThat(response.totalUserCount()).isEqualTo(12L);
    }

    @Test
    @DisplayName("관리자가 아니면 사용자 요약을 조회할 수 없다")
    void getUserSummaryWithNonAdminThrowsException() {
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(user(UserRole.USER, UserStatus.ACTIVE)));

        assertThatThrownBy(() -> adminUserService.getUserSummary(ADMIN_ID))
            .isInstanceOf(AtchagongException.class);
        verify(userRepository, never()).countByUserStatusInAndDeletedAtIsNull(org.mockito.ArgumentMatchers.anyCollection());
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 사용자 요약을 조회할 수 없다")
    void getUserSummaryWithUnknownUserThrowsException() {
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.getUserSummary(ADMIN_ID))
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.USER_NOT_FOUND.name());
    }

    private User user(UserRole role, UserStatus status) {
        return User.builder()
            .id(1L)
            .userId(ADMIN_ID)
            .nickname("admin")
            .userRole(role)
            .userStatus(status)
            .onboardingCompleted(true)
            .build();
    }
}
