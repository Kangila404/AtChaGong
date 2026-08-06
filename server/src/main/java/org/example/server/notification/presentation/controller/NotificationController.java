package org.example.server.notification.presentation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Notification API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notification-settings")
    public ResponseEntity<ApiResponse<NotificationSettingResponse>> getNotificationSetting(
        @AuthenticationPrincipal String userId
    ) {
        NotificationSettingResponse response = notificationService.getNotificationSetting(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/notification-settings")
    public ResponseEntity<ApiResponse<NotificationSettingResponse>> updateNotificationSetting(
        @AuthenticationPrincipal String userId,
        @RequestBody UpdateNotificationSettingRequest request
    ) {
        NotificationSettingResponse response = notificationService.updateNotificationSetting(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/device-tokens")
    public ResponseEntity<ApiResponse<DeviceTokenResponse>> upsertDeviceToken(
        @AuthenticationPrincipal String userId,
        @RequestBody UpsertDeviceTokenRequest request
    ) {
        DeviceTokenResponse response = notificationService.upsertDeviceToken(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/device-tokens")
    public ResponseEntity<Void> deactivateDeviceToken(
        @AuthenticationPrincipal String userId,
        @RequestBody DeleteDeviceTokenRequest request
    ) {
        notificationService.deactivateDeviceToken(userId, request);
        return ResponseEntity.noContent().build();
    }
}
