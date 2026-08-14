package org.example.server.auth.infrastructure.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.exception.AuthErrorCode;
import org.example.server.auth.exception.AuthException;
import org.example.server.auth.presentation.dto.SocialUserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class KakaoSocialAuthProviderTest {

    private KakaoSocialAuthProvider provider;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
            .baseUrl("https://kapi.kakao.com");
        mockServer = MockRestServiceServer.bindTo(builder).build();
        provider = new KakaoSocialAuthProvider(builder.build());
    }

    @Test
    @DisplayName("카카오 access token으로 사용자 정보를 조회해 provider id를 반환한다")
    void verifyWithValidKakaoAccessTokenReturnsProviderId() {
        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
            .andExpect(header("Authorization", "Bearer kakao-access-token"))
            .andRespond(withSuccess("""
                {
                  "id": 12345
                }
                """, MediaType.APPLICATION_JSON));

        SocialUserInfo userInfo = provider.verify("kakao-access-token");

        assertThat(provider.supports()).isEqualTo(AuthType.KAKAO);
        assertThat(userInfo.providerId()).isEqualTo("12345");
        mockServer.verify();
    }

    @Test
    @DisplayName("카카오 사용자 정보 API가 401을 반환하면 INVALID_PROVIDER_TOKEN 예외가 발생한다")
    void verifyWithUnauthorizedKakaoTokenThrowsInvalidProviderToken() {
        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
            .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> provider.verify("invalid-token"))
            .isInstanceOf(AuthException.class)
            .extracting("code")
            .isEqualTo(AuthErrorCode.INVALID_PROVIDER_TOKEN.name());
        mockServer.verify();
    }

    @Test
    @DisplayName("카카오 응답에 id가 없으면 INVALID_PROVIDER_TOKEN 예외가 발생한다")
    void verifyWithoutKakaoIdThrowsInvalidProviderToken() {
        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
            .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> provider.verify("kakao-access-token"))
            .isInstanceOf(AuthException.class)
            .extracting("code")
            .isEqualTo(AuthErrorCode.INVALID_PROVIDER_TOKEN.name());
        mockServer.verify();
    }
}
