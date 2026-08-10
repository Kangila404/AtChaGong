package org.example.server.notice.application;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.AtchagongException;
import org.example.server.common.exception.ErrorCode;
import org.example.server.notice.domain.models.Notice;
import org.example.server.notice.domain.repository.NoticeRepository;
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

    private static final int MAX_PAGE_SIZE = 100;
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public NoticePageResponse getNotices(Integer page, Integer size) {
        validatePageRequest(page, size);
        PageRequest pageRequest = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<Notice> noticePage = noticeRepository.findAll(pageRequest);
        List<NoticeSummaryResponse> content = noticePage.getContent().stream()
            .map(notice -> NoticeSummaryResponse.of(notice, toSeoulOffsetDateTime(notice.getCreatedAt())))
            .toList();
        return NoticePageResponse.of(noticePage, content);
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponse getNotice(Long noticeId) {
        validateNoticeId(noticeId);
        Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new AtchagongException(ErrorCode.NOTICE_NOT_FOUND));

        return NoticeDetailResponse.of(
            notice,
            toSeoulOffsetDateTime(notice.getCreatedAt()),
            toSeoulOffsetDateTime(notice.getUpdatedAt())
        );
    }

    private void validatePageRequest(Integer page, Integer size) {
        if (page == null || page < 0 || size == null || size < 1 || size > MAX_PAGE_SIZE) {
            throw new AtchagongException(ErrorCode.INVALID_PAGE_REQUEST);
        }
    }

    private void validateNoticeId(Long noticeId) {
        if (noticeId == null || noticeId < 1) {
            throw new AtchagongException(ErrorCode.INVALID_NOTICE_ID);
        }
    }

    private OffsetDateTime toSeoulOffsetDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(SEOUL_ZONE).toOffsetDateTime();
    }
}
