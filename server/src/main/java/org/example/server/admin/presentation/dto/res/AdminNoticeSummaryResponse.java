package org.example.server.admin.presentation.dto.res;

import java.time.OffsetDateTime;
import lombok.Builder;
import org.example.server.notice.domain.models.Notice;

@Builder
public record AdminNoticeSummaryResponse(
    Long noticeId,
    String title,
    String status,
    OffsetDateTime publishStartsAt,
    OffsetDateTime publishEndsAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public static AdminNoticeSummaryResponse of(
        Notice notice,
        OffsetDateTime publishStartsAt,
        OffsetDateTime publishEndsAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
    ) {
        return AdminNoticeSummaryResponse.builder()
            .noticeId(notice.getId())
            .title(notice.getTitle())
            .status(notice.getStatus().value())
            .publishStartsAt(publishStartsAt)
            .publishEndsAt(publishEndsAt)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }
}
