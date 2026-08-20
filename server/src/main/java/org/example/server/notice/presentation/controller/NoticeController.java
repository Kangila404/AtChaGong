package org.example.server.notice.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.server.common.response.ApiResponse;
import org.example.server.notice.application.NoticeService;
import org.example.server.notice.presentation.dto.req.NoticePageRequest;
import org.example.server.notice.presentation.dto.res.NoticeDetailResponse;
import org.example.server.notice.presentation.dto.res.NoticePageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<ApiResponse<NoticePageResponse>> getNotices(
        @ModelAttribute NoticePageRequest request
    ) {
        NoticePageResponse response = noticeService.getNotices(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> getNotice(@PathVariable String noticeId) {
        NoticeDetailResponse response = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
