package org.example.server.auth.infrastructure.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import java.net.MalformedURLException;
import java.net.URL;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppleAuthConfig {
    private static final String APPLE_JWK_URL = "https://appleid.apple.com/auth/keys";

    @Bean
    public ConfigurableJWTProcessor<SecurityContext> appleJwtProcessor() throws MalformedURLException {
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        JWKSource<SecurityContext> jwkSource = new RemoteJWKSet<>(new URL(APPLE_JWK_URL));

        jwtProcessor.setJWSKeySelector(
            new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource)
        );

        return jwtProcessor;
    }
}
