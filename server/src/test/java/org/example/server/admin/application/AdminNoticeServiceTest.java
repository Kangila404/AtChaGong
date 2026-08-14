package org.example.server.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.example.server.admin.presentation.dto.req.AdminNoticePageRequest;
import org.example.server.admin.presentation.dto.req.NoticeCreateRequest;
import org.example.server.admin.presentation.dto.req.NoticeUpdateRequest;
import org.example.server.admin.presentation.dto.res.AdminNoticePageResponse;
import org.example.server.admin.presentation.dto.res.NoticeCreateResponse;
import org.example.server.common.exception.AtchagongException;
import org.example.server.notice.application.NoticeQuerySupport;
import org.example.server.notice.domain.enums.NoticeStatus;
import org.example.server.notice.domain.models.Notice;
import org.example.server.notice.domain.repository.NoticeRepository;
import org.example.server.notice.exception.NoticeErrorCode;
import org.example.server.notice.exception.NoticeException;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class AdminNoticeServiceTest {

    private static final String ADMIN_ID = "admin-1";
    private static final long ADMIN_PK = 1L;

    @InjectMocks
    private AdminNoticeService adminNoticeService;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private NoticeQuerySupport noticeQuerySupport = new NoticeQuerySupport();

    @Test
    @DisplayName("관리자는 공지 목록을 조회할 수 있다")
    void getNoticesReturnsNoticePage() {
        Notice notice = notice(1L, "notice", NoticeStatus.PUBLISHED);
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin()));
        given(noticeRepository.findAll(any()))
            .willReturn(new PageImpl<>(List.of(notice), PageRequest.of(0, 20), 1));

        AdminNoticePageResponse response = adminNoticeService.getNotices(
            ADMIN_ID,
            new AdminNoticePageRequest("0", "20", "all")
        );

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).noticeId()).isEqualTo(1L);
        assertThat(response.totalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("관리자는 공지를 생성할 수 있다")
    void createNoticeSavesNotice() {
        OffsetDateTime publishStartsAt = OffsetDateTime.of(2024, 1, 15, 0, 0, 0, 0, ZoneOffset.UTC);
        NoticeCreateRequest request = new NoticeCreateRequest(
            " title ",
            " content ",
            "https://example.com/notice.png",
            "published",
            publishStartsAt,
            null
        );
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin()));
        given(noticeRepository.save(any(Notice.class))).willAnswer(invocation -> invocation.getArgument(0));

        NoticeCreateResponse response = adminNoticeService.createNotice(ADMIN_ID, request);

        ArgumentCaptor<Notice> captor = ArgumentCaptor.forClass(Notice.class);
        verify(noticeRepository).save(captor.capture());
        Notice saved = captor.getValue();
        assertThat(response.noticeId()).isNull();
        assertThat(saved.getAdminId()).isEqualTo(ADMIN_PK);
        assertThat(saved.getTitle()).isEqualTo("title");
        assertThat(saved.getContent()).isEqualTo("content");
        assertThat(saved.getStatus()).isEqualTo(NoticeStatus.PUBLISHED);
        assertThat(saved.getPublishStartsAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 9, 0));
    }

    @Test
    @DisplayName("관리자가 아니면 공지를 생성할 수 없다")
    void createNoticeWithNonAdminThrowsException() {
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(user(UserRole.USER, UserStatus.ACTIVE)));

        assertThatThrownBy(() -> adminNoticeService.createNotice(
            ADMIN_ID,
            new NoticeCreateRequest("title", "content", null, "published", null, null)
        )).isInstanceOf(AtchagongException.class);
        verify(noticeRepository, never()).save(any());
    }

    @Test
    @DisplayName("공지 ID 형식이 올바르지 않으면 공지를 수정할 수 없다")
    void updateNoticeWithInvalidIdThrowsException() {
        given(userRepository.findByUserId(ADMIN_ID)).willReturn(Optional.of(admin()));

        assertThatThrownBy(() -> adminNoticeService.updateNotice(
            ADMIN_ID,
            "abc",
            new NoticeUpdateRequest("title", null, null, null, null, null)
        ))
            .isInstanceOf(NoticeException.class)
            .extracting("code")
            .isEqualTo(NoticeErrorCode.INVALID_NOTICE_ID.name());
        verify(noticeRepository, never()).findById(any());
    }

    private User admin() {
        return user(UserRole.ADMIN, UserStatus.ACTIVE);
    }

    private User user(UserRole role, UserStatus status) {
        return User.builder()
            .id(ADMIN_PK)
            .userId(ADMIN_ID)
            .nickname("admin")
            .userRole(role)
            .userStatus(status)
            .onboardingCompleted(true)
            .build();
    }

    private Notice notice(Long id, String title, NoticeStatus status) {
        Notice notice = org.mockito.Mockito.mock(Notice.class);
        LocalDateTime now = LocalDateTime.now();
        given(notice.getId()).willReturn(id);
        given(notice.getTitle()).willReturn(title);
        given(notice.getStatus()).willReturn(status);
        given(notice.getPublishStartsAt()).willReturn(now);
        given(notice.getPublishEndsAt()).willReturn(null);
        given(notice.getCreatedAt()).willReturn(now);
        given(notice.getUpdatedAt()).willReturn(now);
        return notice;
    }
}