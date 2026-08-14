package org.example.server.auth.infrastructure.oauth;

import lombok.RequiredArgsConstructor;
import org.example.server.auth.application.SocialAuthProvider;
import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.exception.AuthErrorCode;
import org.example.server.auth.exception.AuthException;
import org.example.server.auth.presentation.dto.SocialUserInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class KakaoSocialAuthProvider implements SocialAuthProvider {

    private final RestClient kakaoRestClient;

    @Override
    public AuthType supports() {
        return AuthType.KAKAO;
    }

    @Override
    public SocialUserInfo verify(String credential) {
        try {
            KakaoUserResponse response = kakaoRestClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + credential)
                .retrieve()
                .body(KakaoUserResponse.class);

            if (response == null || response.id() == null) {
                throw new AuthException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
            }

            return new SocialUserInfo(String.valueOf(response.id()));

        } catch (RestClientException exception) {
            throw new AuthException(AuthErrorCode.INVALID_PROVIDER_TOKEN, exception);
        }
    }

    private record KakaoUserResponse(Long id) {}
}