package org.example.server.admin.presentation.dto.res;

import java.time.OffsetDateTime;
import lombok.Builder;
import org.example.server.notice.domain.models.Notice;

@Builder
public record NoticeUpdateResponse(
    Long noticeId,
    String title,
    String content,
    String imgUrl,
    String status,
    OffsetDateTime publishStartsAt,
    OffsetDateTime publishEndsAt
) {
    public static NoticeUpdateResponse of(
        Notice notice,
        OffsetDateTime publishStartsAt,
        OffsetDateTime publishEndsAt
    ) {
        return NoticeUpdateResponse.builder()
            .noticeId(notice.getId())
            .title(notice.getTitle())
            .content(notice.getContent())
            .imgUrl(notice.getImgUrl())
            .status(notice.getStatus().value())
            .publishStartsAt(publishStartsAt)
            .publishEndsAt(publishEndsAt)
            .build();
    }
}
