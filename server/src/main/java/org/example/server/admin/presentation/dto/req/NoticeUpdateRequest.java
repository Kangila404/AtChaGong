package org.example.server.admin.presentation.dto.req;

public record NoticeUpdateRequest(
    String title,
    String content
) {
}
