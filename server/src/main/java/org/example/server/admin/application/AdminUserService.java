package org.example.server.admin.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.admin.presentation.dto.res.AdminUserSummaryResponse;
import org.example.server.common.exception.AtchagongException;
import org.example.server.common.exception.CommonErrorCode;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AdminUserSummaryResponse getUserSummary(String userId) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);

        long totalUserCount = userRepository.countByUserStatusInAndDeletedAtIsNull(List.of(
            UserStatus.ACTIVE,
            UserStatus.SUSPENDED
        ));
        return new AdminUserSummaryResponse(totalUserCount);
    }

    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private void validateAdminUser(User user) {
        if (user.getUserStatus() != UserStatus.ACTIVE || user.getUserRole() != UserRole.ADMIN) {
            throw new AtchagongException(CommonErrorCode.FORBIDDEN);
        }
    }
}
