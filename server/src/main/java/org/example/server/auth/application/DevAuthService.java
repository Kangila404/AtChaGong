package org.example.server.auth.application;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.domain.models.RefreshToken;
import org.example.server.auth.domain.repositories.RefreshTokenRepository;
import org.example.server.auth.infrastructure.jwt.JwtTokenProvider;
import org.example.server.auth.presentation.dto.req.DevLoginRequest;
import org.example.server.auth.presentation.dto.res.DevSignupResponse;
import org.example.server.auth.presentation.dto.res.LoginResponse;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DevAuthService {

    // jwt
    private final JwtTokenProvider jwtTokenProvider;

    // 리포지토리
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public DevSignupResponse signup() {
        String userId = UUID.randomUUID().toString();

        User user = User.builder()
            .userId(userId)
            .nickname("개발유저-" + userId.substring(0, 6))
            .userRole(UserRole.USER)
            .userStatus(UserStatus.ACTIVE)
            .onboardingCompleted(false)
            .build();

        User savedUser = userRepository.save(user);

        return DevSignupResponse.from(savedUser.getUserId());
    }

    @Transactional
    public LoginResponse login(DevLoginRequest request) {
        User user = findUserByUserIdOrThrow(request.userId());
        validateUserStatus(user.getUserStatus());

        String refreshToken = saveRefreshToken(user.getUserId());

        String accessToken = jwtTokenProvider.createAccessToken(
            user.getUserId(),
            user.getUserRole().name()
        );

        return LoginResponse.of(refreshToken, accessToken);
    }

    // ============= 조회 메서드 모음 ============= //

    // 1. (Long) id -> User 조회
    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("유저를 찾을 수 없습니다.")
            );
    }

    // 2. (String) userId -> User 조회
    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() ->
                new IllegalArgumentException("유저를 찾을 수 없습니다.")
            );
    }

    // ============= 검증 메서드 모음 ============= //

    // 1. 유저 상태 검증
    private void validateUserStatus(UserStatus userStatus) {
        if (userStatus != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("비활성화된 유저입니다.");
        }
    }

    // ============= JWT 메서드 모음 ============= //

    // 1. 리프레시 토큰 발급 및 저장
    private String saveRefreshToken(String userId) {
        User user = findUserByUserIdOrThrow(userId);

        String refreshToken =
            jwtTokenProvider.createRefreshToken(userId);

        RefreshToken savedToken =
            refreshTokenRepository.findByUserId(user.getId())
                .orElse(null);

        LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

        if (savedToken == null) {
            refreshTokenRepository.save(
                RefreshToken.builder()
                    .userId(user.getId())
                    .tokenHash(refreshToken)
                    .expiredAt(expiredAt)
                    .build()
            );
        } else {
            savedToken.updateToken(refreshToken, expiredAt);
        }

        return refreshToken;
    }
}