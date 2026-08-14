package org.example.server.notice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.server.notice.domain.enums.NoticeStatus;
import org.example.server.notice.domain.models.Notice;
import org.example.server.notice.domain.repository.NoticeRepository;
import org.example.server.notice.exception.NoticeErrorCode;
import org.example.server.notice.exception.NoticeException;
import org.example.server.notice.presentation.dto.req.NoticePageRequest;
import org.example.server.notice.presentation.dto.res.NoticeDetailResponse;
import org.example.server.notice.presentation.dto.res.NoticePageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @InjectMocks
    private NoticeService noticeService;

    @Mock
    private NoticeRepository noticeRepository;

    @Spy
    private NoticeQuerySupport noticeQuerySupport = new NoticeQuerySupport();

    @Test
    @DisplayName("게시된 공지 목록을 페이지 응답으로 반환한다")
    void getNoticesReturnsPublishedNoticePage() {
        Notice notice = noticeSummary(1L, "notice");
        given(noticeRepository.findByStatus(org.mockito.ArgumentMatchers.eq(NoticeStatus.PUBLISHED), any()))
            .willReturn(new PageImpl<>(List.of(notice), PageRequest.of(0, 20), 1));

        NoticePageResponse response = noticeService.getNotices(new NoticePageRequest("0", "20"));

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).noticeId()).isEqualTo(1L);
        assertThat(response.content().get(0).title()).isEqualTo("notice");
        assertThat(response.totalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("공지 페이지 요청 값이 올바르지 않으면 조회할 수 없다")
    void getNoticesWithInvalidPageThrowsException() {
        assertThatThrownBy(() -> noticeService.getNotices(new NoticePageRequest("-1", "20")))
            .isInstanceOf(NoticeException.class)
            .extracting("code")
            .isEqualTo(NoticeErrorCode.INVALID_PAGE_REQUEST.name());
        verify(noticeRepository, never()).findByStatus(any(), any());
    }

    @Test
    @DisplayName("게시된 공지 상세를 반환한다")
    void getNoticeReturnsPublishedNotice() {
        Notice notice = noticeDetail(1L, "notice");
        given(noticeRepository.findByIdAndStatus(1L, NoticeStatus.PUBLISHED)).willReturn(Optional.of(notice));

        NoticeDetailResponse response = noticeService.getNotice("1");

        assertThat(response.noticeId()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("notice");
        assertThat(response.content()).isEqualTo("content");
    }

    @Test
    @DisplayName("공지 ID 형식이 올바르지 않으면 상세를 조회할 수 없다")
    void getNoticeWithInvalidIdThrowsException() {
        assertThatThrownBy(() -> noticeService.getNotice("abc"))
            .isInstanceOf(NoticeException.class)
            .extracting("code")
            .isEqualTo(NoticeErrorCode.INVALID_NOTICE_ID.name());
        verify(noticeRepository, never()).findByIdAndStatus(any(), any());
    }

    private Notice noticeSummary(Long id, String title) {
        Notice notice = org.mockito.Mockito.mock(Notice.class);
        LocalDateTime now = LocalDateTime.now();
        given(notice.getId()).willReturn(id);
        given(notice.getTitle()).willReturn(title);
        given(notice.getPublishStartsAt()).willReturn(now);
        given(notice.getCreatedAt()).willReturn(now);
        return notice;
    }

    private Notice noticeDetail(Long id, String title) {
        Notice notice = noticeSummary(id, title);
        LocalDateTime now = LocalDateTime.now();
        given(notice.getContent()).willReturn("content");
        given(notice.getImgUrl()).willReturn("https://example.com/notice.png");
        given(notice.getUpdatedAt()).willReturn(now);
        return notice;
    }
}