package org.example.server.notice.presentation.dto.res;

import java.time.OffsetDateTime;
import lombok.Builder;
import org.example.server.notice.domain.models.Notice;

@Builder
public record NoticeDetailResponse(
    Long noticeId,
    String title,
    String content,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public static NoticeDetailResponse of(Notice notice, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return NoticeDetailResponse.builder()
            .noticeId(notice.getId())
            .title(notice.getTitle())
            .content(notice.getContent())
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }
}
