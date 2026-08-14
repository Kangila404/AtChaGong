package org.example.server.admin.presentation.dto.req;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.server.notice.domain.models.Notice;

public record NoticeUpdateRequest(
    @Pattern(regexp = "(?s).*\\S.*")
    @Size(max = Notice.MAX_TITLE_LENGTH)
    String title,

    @Pattern(regexp = "(?s).*\\S.*")
    String content,

    @Size(max = 512)
    String imgUrl,

    @Pattern(regexp = "(?s).*\\S.*")
    String status,

    OffsetDateTime publishStartsAt,

    OffsetDateTime publishEndsAt
) {
    public NoticeUpdateRequest {
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
