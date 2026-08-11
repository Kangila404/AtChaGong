package org.example.server.admin.presentation.dto.res;

import java.util.List;
import lombok.Builder;
import org.example.server.notice.domain.models.Notice;
import org.springframework.data.domain.Page;

@Builder
public record AdminNoticePageResponse(
    List<AdminNoticeSummaryResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static AdminNoticePageResponse of(Page<Notice> page, List<AdminNoticeSummaryResponse> content) {
        return AdminNoticePageResponse.builder()
            .content(content)
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
