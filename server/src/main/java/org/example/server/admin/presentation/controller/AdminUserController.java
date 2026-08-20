package org.example.server.admin.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.server.admin.application.AdminUserService;
import org.example.server.admin.presentation.dto.res.AdminUserSummaryResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AdminUserSummaryResponse>> getUserSummary(
        @AuthenticationPrincipal String userId
    ) {
        AdminUserSummaryResponse response = adminUserService.getUserSummary(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
