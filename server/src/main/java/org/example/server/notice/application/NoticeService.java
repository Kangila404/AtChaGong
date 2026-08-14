package org.example.server.notice.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.notice.domain.enums.NoticeStatus;
import org.example.server.notice.domain.models.Notice;
import org.example.server.notice.domain.repository.NoticeRepository;
import org.example.server.notice.exception.NoticeErrorCode;
import org.example.server.notice.exception.NoticeException;
import org.example.server.notice.presentation.dto.req.NoticePageRequest;
import org.example.server.notice.presentation.dto.res.NoticeDetailResponse;
import org.example.server.notice.presentation.dto.res.NoticePageResponse;
import org.example.server.notice.presentation.dto.res.NoticeSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeQuerySupport noticeQuerySupport;

    @Transactional(readOnly = true)
    public NoticePageResponse getNotices(NoticePageRequest request) {
        PageValues pageValues = parsePageRequest(request);
        PageRequest pageRequest = PageRequest.of(
            pageValues.page(),
            pageValues.size(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<Notice> noticePage = noticeRepository.findByStatus(NoticeStatus.PUBLISHED, pageRequest);
        List<NoticeSummaryResponse> content = noticePage.getContent().stream()
            .map(notice -> NoticeSummaryResponse.of(
                notice,
                noticeQuerySupport.isNew(notice),
                noticeQuerySupport.toSeoulOffsetDateTime(notice.getCreatedAt())
            ))
            .toList();
        return NoticePageResponse.of(noticePage, content);
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponse getNotice(String noticeId) {
        Long parsedNoticeId = noticeQuerySupport.parseNoticeId(noticeId);
        Notice notice = noticeRepository.findByIdAndStatus(parsedNoticeId, NoticeStatus.PUBLISHED)
            .orElseThrow(() -> new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND));

        return NoticeDetailResponse.of(
            notice,
            noticeQuerySupport.isNew(notice),
            noticeQuerySupport.toSeoulOffsetDateTime(notice.getCreatedAt()),
            noticeQuerySupport.toSeoulOffsetDateTime(notice.getUpdatedAt())
        );
    }

    private PageValues parsePageRequest(NoticePageRequest request) {
        if (request == null) {
            throw new NoticeException(NoticeErrorCode.INVALID_PAGE_REQUEST);
        }

        int page = noticeQuerySupport.parsePageValue(request.page(), NoticePageRequest.DEFAULT_PAGE);
        int size = noticeQuerySupport.parsePageValue(request.size(), NoticePageRequest.DEFAULT_SIZE);
        if (page < 0 || size < 1 || size > NoticePageRequest.MAX_SIZE) {
            throw new NoticeException(NoticeErrorCode.INVALID_PAGE_REQUEST);
        }
        return new PageValues(page, size);
    }

    private record PageValues(int page, int size) {
    }
}