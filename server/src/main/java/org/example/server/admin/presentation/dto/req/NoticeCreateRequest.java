package org.example.server.admin.presentation.dto.req;

public record NoticeCreateRequest(
    String title,
    String content
) {
}
