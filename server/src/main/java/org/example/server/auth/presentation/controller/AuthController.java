package org.example.server.auth.presentation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.application.AuthService;
import org.example.server.auth.presentation.dto.req.LoginRequest;
import org.example.server.auth.presentation.dto.req.RefreshTokenRequest;
import org.example.server.auth.presentation.dto.res.LoginResponse;
import org.example.server.auth.presentation.dto.res.LogoutResponse;
import org.example.server.auth.presentation.dto.res.RefreshTokenResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>>reissue(
        @Valid @RequestBody RefreshTokenRequest request
    ){
        RefreshTokenResponse response = authService.reissue(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/social-login")
    public ResponseEntity<ApiResponse<LoginResponse>> socialLogin(
        @Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.socialLogin(request);
        return  ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(@AuthenticationPrincipal String userId){
        LogoutResponse response = authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
