package org.example.server.attendance.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.server.attendance.application.AttendanceService;
import org.example.server.attendance.presentation.dto.res.AttendanceResponse;
import org.example.server.attendance.presentation.dto.res.AttendanceStatusResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "출석 API")
@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/status")
    @Operation(summary = "오늘 출석 여부 및 연속 출석 일수 조회")
    public ResponseEntity<ApiResponse<AttendanceStatusResponse>> getStatus(
        @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getStatus(userId)));
    }

    @PostMapping
    @Operation(summary = "오늘의 출석 및 코인 보상 지급")
    public ResponseEntity<ApiResponse<AttendanceResponse>> attend(
        @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(attendanceService.attend(userId)));
    }
}
