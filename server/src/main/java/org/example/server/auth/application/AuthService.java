package org.example.server.auth.application;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.domain.models.RefreshToken;
import org.example.server.auth.domain.repositories.RefreshTokenRepository;
import org.example.server.auth.infrastructure.jwt.JwtTokenProvider;
import org.example.server.auth.presentation.dto.req.RefreshTokenRequest;
import org.example.server.auth.presentation.dto.res.RefreshTokenResponse;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    // jwt
    private final JwtTokenProvider jwtTokenProvider;
    // 리포지토리
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenResponse reissue(RefreshTokenRequest request){
        RefreshToken refreshToken = findRefreshTokenOrThrow(request.refreshToken());
        validateRefreshTokenExpired(refreshToken);

        User user = findUserByUserIdOrThrow(refreshToken.getUserId());
        validateUserStatus(user.getUserStatus());

        String issuedRefreshToken = saveRefreshToken(user.getId());
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getUserRole().name());
        return RefreshTokenResponse.of(issuedRefreshToken, accessToken);
    }

    // ============= 메서드 모음 ============= //

    // 1. (Long)id -> User 조회
    private User findUserByUserIdOrThrow(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(()-> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    // ============= 검증 메서드 모음 ============= //

    // 1. 유저 상태 검증
    private void validateUserStatus(UserStatus userStatus){
        if(!userStatus.equals(UserStatus.ACTIVE)){
            throw new IllegalArgumentException("비활성화된 유저입니다.");
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
    private String saveRefreshToken(Long userId){
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId).orElse(null);

        if(savedToken == null){
            refreshTokenRepository.save(
                RefreshToken.builder()
                    .userId(userId)
                    .tokenHash(refreshToken)
                    .expiredAt(LocalDateTime.now().plusDays(7))
                    .build());
        } else {
            savedToken.updateToken(refreshToken, LocalDateTime.now().plusDays(7));
        }
        return refreshToken;
    }



}
