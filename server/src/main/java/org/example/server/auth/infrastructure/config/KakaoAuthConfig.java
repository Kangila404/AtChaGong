package org.example.server.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class KakaoAuthConfig {
    @Bean
    public RestClient kakaoRestClient() {
        return RestClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .build();
    }
}
