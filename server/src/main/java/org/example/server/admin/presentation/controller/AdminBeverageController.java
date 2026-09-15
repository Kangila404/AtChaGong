package org.example.server.admin.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.server.admin.application.AdminBeverageService;
import org.example.server.admin.presentation.dto.req.AdminBeverageCreateRequest;
import org.example.server.admin.presentation.dto.req.AdminBeverageOrderRequest;
import org.example.server.admin.presentation.dto.req.AdminBeveragePageRequest;
import org.example.server.admin.presentation.dto.req.AdminBeverageUpdateRequest;
import org.example.server.admin.presentation.dto.res.AdminBeveragePageResponse;
import org.example.server.admin.presentation.dto.res.AdminBeverageResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 음료 API")
@RestController
@RequestMapping("/api/v1/admin/beverages")
@RequiredArgsConstructor
public class AdminBeverageController {

    private final AdminBeverageService adminBeverageService;

    @GetMapping
    @Operation(summary = "관리자 음료 목록 조회")
    public ResponseEntity<ApiResponse<AdminBeveragePageResponse>> getBeverages(
        @AuthenticationPrincipal String userId,
        @ModelAttribute AdminBeveragePageRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(adminBeverageService.getBeverages(userId, request)));
    }

    @GetMapping("/{beverageId}")
    @Operation(summary = "관리자 음료 상세 조회")
    public ResponseEntity<ApiResponse<AdminBeverageResponse>> getBeverage(
        @AuthenticationPrincipal String userId,
        @PathVariable String beverageId
    ) {
        return ResponseEntity.ok(ApiResponse.success(adminBeverageService.getBeverage(userId, beverageId)));
    }

    @PostMapping
    @Operation(summary = "관리자 음료 등록")
    public ResponseEntity<ApiResponse<AdminBeverageResponse>> createBeverage(
        @AuthenticationPrincipal String userId,
        @RequestBody(required = false) AdminBeverageCreateRequest request
    ) {
        AdminBeverageResponse response = adminBeverageService.createBeverage(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PatchMapping("/{beverageId}")
    @Operation(summary = "관리자 음료 수정")
    public ResponseEntity<ApiResponse<AdminBeverageResponse>> updateBeverage(
        @AuthenticationPrincipal String userId,
        @PathVariable String beverageId,
        @RequestBody(required = false) AdminBeverageUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            adminBeverageService.updateBeverage(userId, beverageId, request)
        ));
    }

    @PatchMapping("/order")
    @Operation(summary = "관리자 음료 진열 순서 일괄 변경")
    public ResponseEntity<Void> updateDisplayOrders(
        @AuthenticationPrincipal String userId,
        @RequestBody(required = false) AdminBeverageOrderRequest request
    ) {
        adminBeverageService.updateDisplayOrders(userId, request);
        return ResponseEntity.noContent().build();
    }
}
