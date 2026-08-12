package org.example.server.admin.presentation.dto.req;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.server.notice.domain.models.Notice;

public record NoticeUpdateRequest(
    @Pattern(regexp = "(?s).*\\S.*")
    @Size(max = Notice.MAX_TITLE_LENGTH)
    String title,

    @Pattern(regexp = "(?s).*\\S.*")
    String content
) {
    public NoticeUpdateRequest {
        title = trim(title);
        content = trim(content);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
