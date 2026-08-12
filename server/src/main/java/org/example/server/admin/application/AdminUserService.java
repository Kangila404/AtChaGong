package org.example.server.admin.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.admin.presentation.dto.res.AdminUserSummaryResponse;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    private void validateAdminUser(User user) {
        if (user.getUserStatus() != UserStatus.ACTIVE || user.getUserRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }
    }
}
