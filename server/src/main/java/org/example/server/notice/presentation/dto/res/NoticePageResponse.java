package org.example.server.notice.presentation.dto.res;

import java.util.List;
import lombok.Builder;
import org.example.server.notice.domain.models.Notice;
import org.springframework.data.domain.Page;

@Builder
public record NoticePageResponse(
    List<NoticeSummaryResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static NoticePageResponse of(Page<Notice> page, List<NoticeSummaryResponse> content) {
        return NoticePageResponse.builder()
            .content(content)
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
