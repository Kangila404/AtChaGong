package org.example.server.notice.presentation.dto.res;

import java.time.OffsetDateTime;
import lombok.Builder;
import org.example.server.notice.domain.models.Notice;

@Builder
public record NoticeSummaryResponse(
    Long noticeId,
    String title,
    OffsetDateTime createdAt
) {
    public static NoticeSummaryResponse of(Notice notice, OffsetDateTime createdAt) {
        return NoticeSummaryResponse.builder()
            .noticeId(notice.getId())
            .title(notice.getTitle())
            .createdAt(createdAt)
            .build();
    }
}
