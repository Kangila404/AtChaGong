package org.example.server.notice.application;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.notice.domain.models.Notice;
import org.example.server.notice.domain.repository.NoticeRepository;
import org.example.server.notice.presentation.dto.res.NoticeDetailResponse;
import org.example.server.notice.presentation.dto.res.NoticePageResponse;
import org.example.server.notice.presentation.dto.res.NoticeSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공지를 찾을 수 없습니다."));

        return NoticeDetailResponse.of(
            notice,
            toSeoulOffsetDateTime(notice.getCreatedAt()),
            toSeoulOffsetDateTime(notice.getUpdatedAt())
        );
    }

    private void validatePageRequest(Integer page, Integer size) {
        if (page == null || page < 0 || size == null || size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "페이지 요청 값이 올바르지 않습니다.");
        }
    }

    private void validateNoticeId(Long noticeId) {
        if (noticeId == null || noticeId < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "공지 ID가 올바르지 않습니다.");
        }
    }

    private OffsetDateTime toSeoulOffsetDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(SEOUL_ZONE).toOffsetDateTime();
    }
}
