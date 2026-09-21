package org.example.server.coin.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.server.coin.application.CoinService;
import org.example.server.coin.presentation.dto.res.CoinBalanceResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "코인 API")
@RestController
@RequestMapping("/api/v1/coins")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;

    @GetMapping("/balance")
    @Operation(summary = "내 현재 코인 잔액 조회")
    public ResponseEntity<ApiResponse<CoinBalanceResponse>> getBalance(
        @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(new CoinBalanceResponse(coinService.getBalance(userId))));
    }
}
