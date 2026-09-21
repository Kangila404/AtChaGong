package org.example.server.beverage.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.application.BeverageService;
import org.example.server.beverage.application.BeveragePurchaseService;
import org.example.server.beverage.presentation.dto.res.BeveragePurchaseResponse;
import org.example.server.beverage.presentation.dto.res.BeverageSaleResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "음료 API")
@RestController
@RequestMapping("/api/v1/beverages")
@RequiredArgsConstructor
public class BeverageController {

    private final BeverageService beverageService;
    private final BeveragePurchaseService beveragePurchaseService;

    @Operation(summary = "판매 음료 목록 조회 API")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BeverageSaleResponse>>> getBeverages() {
        List<BeverageSaleResponse> response = beverageService.getBeverages();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "음료 구매 API")
    @PostMapping("/{beverageId}/purchase")
    public ResponseEntity<ApiResponse<BeveragePurchaseResponse>> purchase(
        @AuthenticationPrincipal String userId,
        @PathVariable Long beverageId
    ) {
        BeveragePurchaseResponse response = beveragePurchaseService.purchase(userId, beverageId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

}
