package org.example.server.beverage.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.application.BeverageService;
import org.example.server.beverage.presentation.dto.res.BeverageResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "음료 API")
@RestController
@RequestMapping("/api/v1/beverages")
@RequiredArgsConstructor
public class BeverageController {

    private final BeverageService beverageService;

    @Operation(summary = "음료 조회 API")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BeverageResponse>>> getBeverages(){
        List<BeverageResponse> response = beverageService.getBeverages();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
