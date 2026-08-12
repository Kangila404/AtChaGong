package org.example.server.admin.presentation.dto.res;

import lombok.Builder;
import org.example.server.notice.domain.models.Notice;

@Builder
public record NoticeUpdateResponse(
    Long noticeId,
    String title,
    String content
) {
    public static NoticeUpdateResponse from(Notice notice) {
        return NoticeUpdateResponse.builder()
            .noticeId(notice.getId())
            .title(notice.getTitle())
            .content(notice.getContent())
            .build();
    }
}
