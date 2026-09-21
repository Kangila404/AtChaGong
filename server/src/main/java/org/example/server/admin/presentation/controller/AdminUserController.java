package org.example.server.admin.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.admin.application.AdminUserService;
import org.example.server.admin.presentation.dto.req.AdminUpdateUserStatusRequest;
import org.example.server.admin.presentation.dto.req.AdminCoinTransactionPageRequest;
import org.example.server.admin.presentation.dto.res.AdminCoinTransactionPageResponse;
import org.example.server.admin.presentation.dto.res.AdminUpdateStatusResponse;
import org.example.server.admin.presentation.dto.res.AdminUserResponse;
import org.example.server.admin.presentation.dto.res.AdminUserSummaryResponse;
import org.example.server.admin.presentation.dto.res.AdminUsersResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 유저 API")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "유저 목록 조회")
    public ResponseEntity<ApiResponse<AdminUsersResponse>> getUsers(
        @AuthenticationPrincipal String adminId
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(adminUserService.getUsers(adminId))
        );
    }

    @GetMapping("/summary")
    @Operation(summary = "가입 유저 수 조회")
    public ResponseEntity<ApiResponse<AdminUserSummaryResponse>> getUserSummary(
        @AuthenticationPrincipal String adminId
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(adminUserService.getUserSummary(adminId))
        );
    }

    @GetMapping("/{userId}")
    @Operation(summary = "유저 상세 조회")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUser(
        @AuthenticationPrincipal String adminId,
        @PathVariable String userId
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(adminUserService.getUser(adminId, userId))
        );
    }

    @GetMapping("/{userId}/coin-transactions")
    @Operation(summary = "사용자 코인 거래 내역 조회")
    public ResponseEntity<ApiResponse<AdminCoinTransactionPageResponse>> getCoinTransactions(
        @AuthenticationPrincipal String adminId,
        @PathVariable String userId,
        @ModelAttribute AdminCoinTransactionPageRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            adminUserService.getCoinTransactions(adminId, userId, request)
        ));
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "유저 상태 변경")
    public ResponseEntity<ApiResponse<AdminUpdateStatusResponse>> updateStatus(
        @AuthenticationPrincipal String adminId,
        @PathVariable String userId,
        @Valid @RequestBody AdminUpdateUserStatusRequest request
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(
                adminUserService.updateStatus(adminId, userId, request)
            )
        );
    }
}
