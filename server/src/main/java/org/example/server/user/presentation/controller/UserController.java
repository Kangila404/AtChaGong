package org.example.server.user.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.common.response.ApiResponse;
import org.example.server.user.application.UserService;
import org.example.server.user.presentation.dto.req.OnboardingRequest;
import org.example.server.user.presentation.dto.req.UpdateNicknameRequest;
import org.example.server.user.presentation.dto.req.UserMeRequest;
import org.example.server.user.presentation.dto.res.OnboardingResponse;
import org.example.server.user.presentation.dto.res.UpdateNicknameResponse;
import org.example.server.user.presentation.dto.res.UserMeResponse;
import org.example.server.user.presentation.dto.res.WithdrawUserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 정보 조회 API")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponse>> getMe(
        @AuthenticationPrincipal String userId
    ){
        UserMeResponse response = userService.getMe(userId);
        return ResponseEntity.ok(ApiResponse.success((response)));
    }

    @Operation(summary = "닉네임 갱신 API")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UpdateNicknameResponse>> updateNickname(
        @AuthenticationPrincipal String userId,
        UpdateNicknameRequest request){
        UpdateNicknameResponse response = userService.updateNickname(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "회원탈퇴 API")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<WithdrawUserResponse>> withdraw(@AuthenticationPrincipal String userId){
        WithdrawUserResponse response = userService.withdraw(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "온보딩 API")
    @PatchMapping("/me/onboarding")
    public ResponseEntity<ApiResponse<OnboardingResponse>> onboarding(
        @AuthenticationPrincipal String userId,
        OnboardingRequest request){
        OnboardingResponse response = userService.onboarding(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
