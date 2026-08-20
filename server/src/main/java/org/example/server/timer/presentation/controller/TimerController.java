package org.example.server.timer.presentation.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/timer/settings")
@RequiredArgsConstructor
public class TimerController {

    private final TimerSettingService timerSettingService;

    @GetMapping
    public ResponseEntity<ApiResponse<TimerSettingResponse>> getSetting(@AuthenticationPrincipal String userId){
        TimerSettingResponse response = timerSettingService.getSetting(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<SaveTimerResponse>> saveSetting(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody SaveTimerRequest request
    ){
        SaveTimerResponse response = timerSettingService.saveSetting(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
