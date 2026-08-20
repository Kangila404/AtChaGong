package org.example.server.auth.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.application.AppReviewAuthService;
import org.example.server.auth.presentation.dto.req.AppReviewLoginRequest;
import org.example.server.auth.presentation.dto.res.LoginResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/app-review")
@RequiredArgsConstructor
public class AppReviewAuthController {

    private final AppReviewAuthService appReviewAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @Valid @RequestBody AppReviewLoginRequest request
    ) {
        LoginResponse response = appReviewAuthService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
