package org.example.server.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.infrastructure.jwt.JwtFilter;
import org.example.server.auth.infrastructure.jwt.JwtTokenProvider;
import org.example.server.common.exception.ErrorCode;
import org.example.server.common.exception.ErrorResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()  // 로그인, 회원가입은 토큰 없이 허용
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**"
                ).permitAll() // 스웨거 통과
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/dev/**").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()                // 나머지 토큰
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) ->
                    writeErrorResponse(response, ErrorCode.UNAUTHORIZED))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    writeErrorResponse(response, ErrorCode.FORBIDDEN))
            )
            .addFilterBefore(new JwtFilter(jwtTokenProvider),  // JwtFilter 등록
                UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.failure(ErrorResponse.of(errorCode)));
    }
}
