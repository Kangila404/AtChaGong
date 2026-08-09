package org.example.server.timer.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.server.common.response.ApiResponse;
import org.example.server.timer.application.TimerSettingService;
import org.example.server.timer.presentation.dto.req.SaveTimerRequest;
import org.example.server.timer.presentation.dto.res.SaveTimerResponse;
import org.example.server.timer.presentation.dto.res.TimerSettingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "타이머 API")
@RestController
@RequestMapping("/api/v1/timer/settings")
@RequiredArgsConstructor
public class TimerController {

    private final TimerSettingService timerSettingService;

    @Operation(summary = "타이머 설정 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<TimerSettingResponse>> getSetting(@AuthenticationPrincipal String userId){
        TimerSettingResponse response = timerSettingService.getSetting(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "타이머 설정 저장")
    @PutMapping
    public ResponseEntity<ApiResponse<SaveTimerResponse>> saveSetting(
        @AuthenticationPrincipal String userId,
        SaveTimerRequest request
    ){
        SaveTimerResponse response = timerSettingService.saveSetting(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
