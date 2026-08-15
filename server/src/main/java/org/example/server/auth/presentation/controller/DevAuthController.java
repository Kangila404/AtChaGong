package org.example.server.auth.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.server.auth.application.DevAuthService;
import org.example.server.auth.presentation.dto.req.DevLoginRequest;
import org.example.server.auth.presentation.dto.res.DevSignupResponse;
import org.example.server.auth.presentation.dto.res.LoginResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "개발용 로그인 APi, 개발 끝나고 반드시 지울 것")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/dev")
public class DevAuthController {

    private final DevAuthService devAuthService;

    @Operation(summary = "mock 로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @RequestBody DevLoginRequest request
    ) {
        LoginResponse response = devAuthService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

//    @Operation(summary = "mock 회원가입")
//    @PostMapping("/signup")
//    public ResponseEntity<ApiResponse<DevSignupResponse>> signup() {
//        DevSignupResponse response = devAuthService.signup();
//
//        return ResponseEntity.ok(
//            ApiResponse.success(response)
//        );
//    }

}
