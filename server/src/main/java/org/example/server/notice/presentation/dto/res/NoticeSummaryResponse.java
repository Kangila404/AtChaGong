package org.example.server.notice.presentation.dto.res;

import java.time.OffsetDateTime;
import lombok.Builder;
import org.example.server.notice.domain.models.Notice;

@Builder
public record NoticeSummaryResponse(
    Long noticeId,
    String title,
    boolean isNew,
    OffsetDateTime createdAt
) {
    public static NoticeSummaryResponse of(Notice notice, boolean isNew, OffsetDateTime createdAt) {
        return NoticeSummaryResponse.builder()
            .noticeId(notice.getId())
            .title(notice.getTitle())
            .isNew(isNew)
            .createdAt(createdAt)
            .build();
    }
}
