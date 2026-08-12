package org.example.server.admin.presentation.dto.req;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.server.notice.domain.models.Notice;

public record NoticeCreateRequest(
    @NotBlank
    @Size(max = Notice.MAX_TITLE_LENGTH)
    String title,

    @NotBlank
    String content,

    @Size(max = 512)
    String imgUrl,

    @NotBlank
    String status,

    OffsetDateTime publishStartsAt,

    OffsetDateTime publishEndsAt
) {
    public NoticeCreateRequest {
        title = trim(title);
        content = trim(content);
        imgUrl = trimToNull(imgUrl);
        status = trim(status);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static String trimToNull(String value) {
        String trimmedValue = trim(value);
        return trimmedValue == null || trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
