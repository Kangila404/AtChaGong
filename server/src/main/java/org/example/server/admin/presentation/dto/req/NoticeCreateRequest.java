package org.example.server.admin.presentation.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.server.notice.domain.models.Notice;

public record NoticeCreateRequest(
    @NotBlank
    @Size(max = Notice.MAX_TITLE_LENGTH)
    String title,

    @NotBlank
    String content
) {
    public NoticeCreateRequest {
        title = trim(title);
        content = trim(content);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
