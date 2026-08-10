package org.example.server.notice.presentation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.server.common.response.ApiResponse;
import org.example.server.notice.application.NoticeService;
import org.example.server.notice.presentation.dto.res.NoticeDetailResponse;
import org.example.server.notice.presentation.dto.res.NoticePageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공지 API")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<ApiResponse<NoticePageResponse>> getNotices(
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size
    ) {
        NoticePageResponse response = noticeService.getNotices(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> getNotice(@PathVariable Long noticeId) {
        NoticeDetailResponse response = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
