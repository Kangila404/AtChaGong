package org.example.server.record.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.common.response.ApiResponse;
import org.example.server.record.application.FocusRecordService;
import org.example.server.record.presentation.dto.req.CreateFocusRecordRequest;
import org.example.server.record.presentation.dto.res.DailyFocusRecordResponse;
import org.example.server.record.presentation.dto.res.FocusRecordResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "집중 기록 API")
@RestController
@RequestMapping("/api/v1/focus-records")
@RequiredArgsConstructor
public class FocusRecordController {

    private final FocusRecordService focusRecordService;

    @PostMapping
    @Operation(summary = "집중 완료 기록 저장")
    public ResponseEntity<ApiResponse<FocusRecordResponse>> createFocusRecord(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody CreateFocusRecordRequest request
    ) {
        FocusRecordResponse response = focusRecordService.createFocusRecord(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/daily")
    @Operation(summary = "일별 집중 기록 조회")
    public ResponseEntity<ApiResponse<DailyFocusRecordResponse>> getDailyFocusRecords(
        @AuthenticationPrincipal String userId,
        @RequestParam String date
    ) {
        DailyFocusRecordResponse response = focusRecordService.getDailyFocusRecords(userId, date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
