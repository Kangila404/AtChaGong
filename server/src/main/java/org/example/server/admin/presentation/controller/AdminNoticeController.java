package org.example.server.admin.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.server.admin.application.AdminNoticeService;
import org.example.server.admin.presentation.dto.req.AdminNoticePageRequest;
import org.example.server.admin.presentation.dto.req.NoticeCreateRequest;
import org.example.server.admin.presentation.dto.req.NoticeUpdateRequest;
import org.example.server.admin.presentation.dto.res.AdminNoticeDetailResponse;
import org.example.server.admin.presentation.dto.res.AdminNoticePageResponse;
import org.example.server.admin.presentation.dto.res.NoticeCreateResponse;
import org.example.server.admin.presentation.dto.res.NoticeUpdateResponse;
import org.example.server.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping
    @Operation(summary = "관리자 공지 목록 조회")
    public ResponseEntity<ApiResponse<AdminNoticePageResponse>> getNotices(
        @AuthenticationPrincipal String userId,
        @ModelAttribute AdminNoticePageRequest request
    ) {
        AdminNoticePageResponse response = adminNoticeService.getNotices(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "관리자 공지 상세 조회")
    public ResponseEntity<ApiResponse<AdminNoticeDetailResponse>> getNotice(
        @AuthenticationPrincipal String userId,
        @PathVariable String noticeId
    ) {
        AdminNoticeDetailResponse response = adminNoticeService.getNotice(userId, noticeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "관리자 공지 등록")
    public ResponseEntity<ApiResponse<NoticeCreateResponse>> createNotice(
        @AuthenticationPrincipal String userId,
        @RequestBody(required = false) NoticeCreateRequest request
    ) {
        NoticeCreateResponse response = adminNoticeService.createNotice(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PatchMapping("/{noticeId}")
    @Operation(summary = "관리자 공지 수정")
    public ResponseEntity<ApiResponse<NoticeUpdateResponse>> updateNotice(
        @AuthenticationPrincipal String userId,
        @PathVariable String noticeId,
        @RequestBody(required = false) NoticeUpdateRequest request
    ) {
        NoticeUpdateResponse response = adminNoticeService.updateNotice(userId, noticeId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{noticeId}")
    @Operation(summary = "관리자 공지 삭제")
    public ResponseEntity<Void> deleteNotice(
        @AuthenticationPrincipal String userId,
        @PathVariable String noticeId
    ) {
        adminNoticeService.deleteNotice(userId, noticeId);
        return ResponseEntity.noContent().build();
    }
}
