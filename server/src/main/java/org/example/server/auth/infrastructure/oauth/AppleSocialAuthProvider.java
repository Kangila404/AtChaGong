package org.example.server.auth.infrastructure.oauth;

import org.springframework.beans.factory.annotation.Value;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import org.example.server.auth.application.SocialAuthProvider;
import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.presentation.dto.SocialUserInfo;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.stereotype.Component;



@Component
public class AppleSocialAuthProvider implements SocialAuthProvider {

    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    private final ConfigurableJWTProcessor<SecurityContext> appleJwtProcessor;
    private final String clientId;

    public AppleSocialAuthProvider(
        ConfigurableJWTProcessor<SecurityContext> appleJwtProcessor,
        @Value("${oauth.apple.client-id}") String clientId
    ) {
        this.appleJwtProcessor = appleJwtProcessor;
        this.clientId = clientId;
    }

    @Override
    public AuthType supports() {
        return AuthType.APPLE;
    }

    @Override
    public SocialUserInfo verify(String credential) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(credential);
            JWTClaimsSet claims = appleJwtProcessor.process(signedJWT, null);

            validateIssuer(claims);
            validateAudience(claims);

            return new SocialUserInfo(claims.getSubject());

        } catch (Exception exception) {
            throw new IllegalArgumentException("Apple 로그인 인증에 실패했습니다.", exception);
        }
    }

    private void validateIssuer(JWTClaimsSet claims) {
        if (!APPLE_ISSUER.equals(claims.getIssuer())) {
            throw new IllegalArgumentException("유효하지 않은 발급자입니다.");
        }
    }

    private void validateAudience(JWTClaimsSet claims) {
        if (!claims.getAudience().contains(clientId)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }
}