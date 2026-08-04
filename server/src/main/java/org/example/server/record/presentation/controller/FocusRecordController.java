package org.example.server.record.presentation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.record.application.FocusRecordService;
import org.example.server.record.presentation.dto.req.CreateFocusRecordRequest;
import org.example.server.record.presentation.dto.res.DailyFocusRecordResponse;
import org.example.server.record.presentation.dto.res.FocusRecordResponse;
import org.example.server.record.presentation.dto.res.MonthlyFocusRecordResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Record API")
@RestController
@RequestMapping("/api/v1/focus-records")
@RequiredArgsConstructor
public class FocusRecordController {

    private final FocusRecordService focusRecordService;

    @PostMapping
    public ResponseEntity<FocusRecordResponse> createFocusRecord(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody CreateFocusRecordRequest request
    ) {
        FocusRecordResponse response = focusRecordService.createFocusRecord(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/daily")
    public ResponseEntity<DailyFocusRecordResponse> getDailyFocusRecords(
        @AuthenticationPrincipal String userId,
        @RequestParam String date
    ) {
        DailyFocusRecordResponse response = focusRecordService.getDailyFocusRecords(userId, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyFocusRecordResponse> getMonthlyFocusRecords(
        @AuthenticationPrincipal String userId,
        @RequestParam String year,
        @RequestParam String month
    ) {
        MonthlyFocusRecordResponse response = focusRecordService.getMonthlyFocusRecords(userId, year, month);
        return ResponseEntity.ok(response);
    }
}
