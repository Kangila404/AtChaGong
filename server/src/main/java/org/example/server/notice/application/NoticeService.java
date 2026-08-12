package org.example.server.notice.application;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final int NEW_BADGE_DAYS = 7;

    private final NoticeRepository noticeRepository;

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
                isNew(notice),
                toSeoulOffsetDateTime(notice.getCreatedAt())
            ))
            .toList();
        return NoticePageResponse.of(noticePage, content);
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponse getNotice(String noticeId) {
        Long parsedNoticeId = parseNoticeId(noticeId);
        Notice notice = noticeRepository.findByIdAndStatus(parsedNoticeId, NoticeStatus.PUBLISHED)
            .orElseThrow(() -> new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND));

        return NoticeDetailResponse.of(
            notice,
            isNew(notice),
            toSeoulOffsetDateTime(notice.getCreatedAt()),
            toSeoulOffsetDateTime(notice.getUpdatedAt())
        );
    }

    private PageValues parsePageRequest(NoticePageRequest request) {
        if (request == null) {
            throw new NoticeException(NoticeErrorCode.INVALID_PAGE_REQUEST);
        }

        int page = parsePageValue(request.page(), NoticePageRequest.DEFAULT_PAGE);
        int size = parsePageValue(request.size(), NoticePageRequest.DEFAULT_SIZE);
        if (page < 0 || size < 1 || size > NoticePageRequest.MAX_SIZE) {
            throw new NoticeException(NoticeErrorCode.INVALID_PAGE_REQUEST);
        }
        return new PageValues(page, size);
    }

    private int parsePageValue(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new NoticeException(NoticeErrorCode.INVALID_PAGE_REQUEST);
        }
    }

    private Long parseNoticeId(String noticeId) {
        if (noticeId == null || !noticeId.matches("\\d+")) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_ID);
        }

        Long parsedNoticeId;
        try {
            parsedNoticeId = Long.parseLong(noticeId);
        } catch (NumberFormatException exception) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_ID);
        }

        if (parsedNoticeId < 1) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_ID);
        }
        return parsedNoticeId;
    }

    private OffsetDateTime toSeoulOffsetDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(SEOUL_ZONE).toOffsetDateTime();
    }

    private boolean isNew(Notice notice) {
        LocalDateTime newBadgeEndsAt = notice.getPublishStartsAt().plusDays(NEW_BADGE_DAYS);
        return !LocalDateTime.now(SEOUL_ZONE).isAfter(newBadgeEndsAt);
    }

    private record PageValues(int page, int size) {
    }
}
