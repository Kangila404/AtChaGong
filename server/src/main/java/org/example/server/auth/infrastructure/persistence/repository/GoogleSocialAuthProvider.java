package org.example.server.auth.infrastructure.persistence.repository;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.application.SocialAuthProvider;
import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.presentation.dto.SocialUserInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleSocialAuthProvider implements SocialAuthProvider {

    private final GoogleIdTokenVerifier verifier;

    @Override
    public AuthType supports() {
        return AuthType.GOOGLE;
    }

    @Override
    public SocialUserInfo verify(String credential) {
        try {
            GoogleIdToken idToken = verifier.verify(credential);

            if(idToken==null){
                throw new IllegalArgumentException("유효하지 않은 토근입니다.");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            return new SocialUserInfo(payload.getSubject());

        } catch (GeneralSecurityException | IOException exception){
            throw new IllegalArgumentException(
                "Google 로그인 인증에 실패했습니다.",
                exception
            );
        }

    }
}
