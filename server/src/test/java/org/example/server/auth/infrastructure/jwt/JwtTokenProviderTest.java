package org.example.server.auth.infrastructure.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private static final String SECRET = "12345678901234567890123456789012";

    @Test
    @DisplayName("액세스 토큰에 사용자 ID와 권한을 담아 발급한다")
    void createAccessTokenContainsUserIdAndRole() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000, 3_600_000);

        String token = provider.createAccessToken("user-1", "USER");

        Claims claims = provider.parseClaims(token);
        assertThat(provider.getUserId(token)).isEqualTo("user-1");
        assertThat(provider.getRole(token)).isEqualTo("USER");
        assertThat(provider.isAccessToken(claims)).isTrue();
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰은 액세스 토큰으로 판정하지 않는다")
    void createRefreshTokenIsNotAccessToken() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000, 3_600_000);

        String token = provider.createRefreshToken("user-1");

        assertThat(provider.getUserId(token)).isEqualTo("user-1");
        assertThat(provider.isAccessToken(provider.parseClaims(token))).isFalse();
    }

    @Test
    @DisplayName("잘못된 토큰은 유효하지 않다")
    void invalidTokenReturnsFalse() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000, 3_600_000);

        assertThat(provider.validateToken("invalid-token")).isFalse();
    }
}
