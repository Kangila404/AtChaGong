package org.example.server.admin.presentation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.admin.application.AdminNoticeService;
import org.example.server.admin.presentation.dto.req.NoticeCreateRequest;
import org.example.server.admin.presentation.dto.req.NoticeUpdateRequest;
import org.example.server.admin.presentation.dto.res.NoticeCreateResponse;
import org.example.server.admin.presentation.dto.res.NoticeUpdateResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 공지 API")
@RestController
@RequestMapping("/api/v1/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;

    @PostMapping
    public ResponseEntity<ApiResponse<NoticeCreateResponse>> createNotice(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody NoticeCreateRequest request
    ) {
        NoticeCreateResponse response = adminNoticeService.createNotice(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PatchMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeUpdateResponse>> updateNotice(
        @AuthenticationPrincipal String userId,
        @PathVariable Long noticeId,
        @Valid @RequestBody NoticeUpdateRequest request
    ) {
        NoticeUpdateResponse response = adminNoticeService.updateNotice(userId, noticeId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(
        @AuthenticationPrincipal String userId,
        @PathVariable Long noticeId
    ) {
        adminNoticeService.deleteNotice(userId, noticeId);
        return ResponseEntity.noContent().build();
    }
}
