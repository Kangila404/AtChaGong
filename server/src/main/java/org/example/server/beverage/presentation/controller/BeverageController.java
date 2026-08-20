package org.example.server.beverage.presentation.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.application.BeverageService;
import org.example.server.beverage.presentation.dto.res.BeverageResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/beverages")
@RequiredArgsConstructor
public class BeverageController {

    private final BeverageService beverageService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BeverageResponse>>> getBeverages(){
        List<BeverageResponse> response = beverageService.getBeverages();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
