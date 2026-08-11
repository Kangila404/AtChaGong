package org.example.server.notification.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.common.response.ApiResponse;
import org.example.server.notification.application.NotificationService;
import org.example.server.notification.presentation.dto.req.DeleteDeviceTokenRequest;
import org.example.server.notification.presentation.dto.req.UpdateNotificationSettingRequest;
import org.example.server.notification.presentation.dto.req.UpsertDeviceTokenRequest;
import org.example.server.notification.presentation.dto.res.DeviceTokenResponse;
import org.example.server.notification.presentation.dto.res.NotificationSettingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notification-settings")
    @Operation(summary = "알림 설정 조회")
    public ResponseEntity<ApiResponse<NotificationSettingResponse>> getNotificationSetting(
        @AuthenticationPrincipal String userId
    ) {
        NotificationSettingResponse response = notificationService.getNotificationSetting(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/notification-settings")
    @Operation(summary = "알림 설정 수정")
    public ResponseEntity<ApiResponse<NotificationSettingResponse>> updateNotificationSetting(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody UpdateNotificationSettingRequest request
    ) {
        NotificationSettingResponse response = notificationService.updateNotificationSetting(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/device-tokens")
    @Operation(summary = "기기 토큰 등록 또는 갱신")
    public ResponseEntity<ApiResponse<DeviceTokenResponse>> upsertDeviceToken(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody UpsertDeviceTokenRequest request
    ) {
        DeviceTokenResponse response = notificationService.upsertDeviceToken(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/device-tokens")
    @Operation(summary = "기기 토큰 비활성화")
    public ResponseEntity<Void> deactivateDeviceToken(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody DeleteDeviceTokenRequest request
    ) {
        notificationService.deactivateDeviceToken(userId, request);
        return ResponseEntity.noContent().build();
    }
}
