package org.example.server.common.config;

import lombok.RequiredArgsConstructor;
import org.example.server.auth.infrastructure.jwt.JwtAccessDeniedHandler;
import org.example.server.auth.infrastructure.jwt.JwtAuthenticationEntryPoint;
import org.example.server.auth.infrastructure.jwt.JwtFilter;
import org.example.server.auth.infrastructure.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/dev/**").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 인증 자체가 안 됨 → 401
                .accessDeniedHandler(jwtAccessDeniedHandler)            // 인증은 됐는데 권한 없음(hasRole 등) → 403
            )
            .addFilterBefore(new JwtFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}