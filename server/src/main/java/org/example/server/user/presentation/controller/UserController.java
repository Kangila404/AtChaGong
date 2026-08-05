package org.example.server.user.presentation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.common.response.ApiResponse;
import org.example.server.user.application.UserService;
import org.example.server.user.presentation.dto.req.UserMeRequest;
import org.example.server.user.presentation.dto.res.UserMeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponse>> getMe(
        @AuthenticationPrincipal String userId
    ){
        UserMeResponse response = userService.getMe(userId);
        return ResponseEntity.ok(ApiResponse.success((response)));
    }


}
