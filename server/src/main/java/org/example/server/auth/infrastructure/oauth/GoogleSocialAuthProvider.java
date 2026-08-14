package org.example.server.auth.infrastructure.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.application.SocialAuthProvider;
import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.exception.AuthErrorCode;
import org.example.server.auth.exception.AuthException;
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

            if (idToken == null) {
                throw new AuthException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            return new SocialUserInfo(payload.getSubject());

        } catch (GeneralSecurityException | IOException exception) {
            throw new AuthException(AuthErrorCode.INVALID_PROVIDER_TOKEN, exception);
        }
    }
}