package org.example.server.admin.presentation.dto.res;

import java.time.OffsetDateTime;
import lombok.Builder;
import org.example.server.notice.domain.models.Notice;

@Builder
public record AdminNoticeDetailResponse(
    Long noticeId,
    String title,
    String content,
    String imgUrl,
    String status,
    boolean isNew,
    OffsetDateTime publishStartsAt,
    OffsetDateTime publishEndsAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public static AdminNoticeDetailResponse of(
        Notice notice,
        boolean isNew,
        OffsetDateTime publishStartsAt,
        OffsetDateTime publishEndsAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
    ) {
        return AdminNoticeDetailResponse.builder()
            .noticeId(notice.getId())
            .title(notice.getTitle())
            .content(notice.getContent())
            .imgUrl(notice.getImgUrl())
            .status(notice.getStatus().value())
            .isNew(isNew)
            .publishStartsAt(publishStartsAt)
            .publishEndsAt(publishEndsAt)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }
}
