package org.example.server.admin.presentation.dto.res;

import lombok.Builder;
import org.example.server.notice.domain.models.Notice;

@Builder
public record NoticeCreateResponse(
    Long noticeId
) {
    public static NoticeCreateResponse from(Notice notice) {
        return NoticeCreateResponse.builder()
            .noticeId(notice.getId())
            .build();
    }
}
