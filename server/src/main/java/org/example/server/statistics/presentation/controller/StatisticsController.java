package org.example.server.statistics.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.server.common.response.ApiResponse;
import org.example.server.statistics.application.StatisticsService;
import org.example.server.statistics.presentation.dto.req.StatisticsRequest;
import org.example.server.statistics.presentation.dto.res.CalendarResponse;
import org.example.server.statistics.presentation.dto.res.StatisticsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "통계 API")
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "전체 통계 요약")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getSummary(
        @AuthenticationPrincipal String userId,
        StatisticsRequest request
    ){
        StatisticsResponse response = statisticsService.getStatistics(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "월별 통계 요약")
    @GetMapping("/calendar/{year}/{month}")
    public ResponseEntity<ApiResponse<CalendarResponse>> getMonth(
        @AuthenticationPrincipal String userId,
        @PathVariable("year") int year,
        @PathVariable("month") int month
    ){
        CalendarResponse response = statisticsService.getCalendar(userId, year, month);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
