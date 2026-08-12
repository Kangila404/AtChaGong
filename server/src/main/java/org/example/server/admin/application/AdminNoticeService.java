package org.example.server.admin.application;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.admin.presentation.dto.req.AdminNoticePageRequest;
import org.example.server.admin.presentation.dto.req.NoticeCreateRequest;
import org.example.server.admin.presentation.dto.req.NoticeUpdateRequest;
import org.example.server.admin.presentation.dto.res.AdminNoticeDetailResponse;
import org.example.server.admin.presentation.dto.res.AdminNoticePageResponse;
import org.example.server.admin.presentation.dto.res.AdminNoticeSummaryResponse;
import org.example.server.admin.presentation.dto.res.NoticeCreateResponse;
import org.example.server.admin.presentation.dto.res.NoticeUpdateResponse;
import org.example.server.notice.domain.enums.NoticeStatus;
import org.example.server.notice.domain.models.Notice;
import org.example.server.notice.domain.repository.NoticeRepository;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final int NEW_BADGE_DAYS = 7;

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AdminNoticePageResponse getNotices(String userId, AdminNoticePageRequest request) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        validateAdminNoticePageRequest(request);

        PageRequest pageRequest = PageRequest.of(
            request.page(),
            request.size(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<Notice> noticePage = isAllStatus(request.status())
            ? noticeRepository.findAll(pageRequest)
            : noticeRepository.findByStatus(NoticeStatus.from(request.status()), pageRequest);

        List<AdminNoticeSummaryResponse> content = noticePage.getContent().stream()
            .map(notice -> AdminNoticeSummaryResponse.of(
                notice,
                toSeoulOffsetDateTime(notice.getPublishStartsAt()),
                toNullableSeoulOffsetDateTime(notice.getPublishEndsAt()),
                toSeoulOffsetDateTime(notice.getCreatedAt()),
                toSeoulOffsetDateTime(notice.getUpdatedAt())
            ))
            .toList();

        return AdminNoticePageResponse.of(noticePage, content);
    }

    @Transactional(readOnly = true)
    public AdminNoticeDetailResponse getNotice(String userId, Long noticeId) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        validateNoticeId(noticeId);

        Notice notice = findNoticeByIdOrThrow(noticeId);
        return AdminNoticeDetailResponse.of(
            notice,
            isNew(notice),
            toSeoulOffsetDateTime(notice.getPublishStartsAt()),
            toNullableSeoulOffsetDateTime(notice.getPublishEndsAt()),
            toSeoulOffsetDateTime(notice.getCreatedAt()),
            toSeoulOffsetDateTime(notice.getUpdatedAt())
        );
    }

    @Transactional
    public NoticeCreateResponse createNotice(String userId, NoticeCreateRequest request) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        NoticeFields fields = validateCreateRequest(request);

        Notice notice = Notice.create(
            admin.getId(),
            fields.title(),
            fields.content(),
            fields.imgUrl(),
            fields.status(),
            fields.publishStartsAt(),
            fields.publishEndsAt()
        );
        Notice savedNotice = noticeRepository.save(notice);
        return NoticeCreateResponse.from(savedNotice);
    }

    @Transactional
    public NoticeUpdateResponse updateNotice(String userId, Long noticeId, NoticeUpdateRequest request) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        validateNoticeId(noticeId);
        NoticeFields fields = validateUpdateRequest(request);

        Notice notice = findNoticeByIdOrThrow(noticeId);
        notice.update(
            fields.title(),
            fields.content(),
            fields.imgUrl(),
            fields.status(),
            fields.publishStartsAt(),
            fields.publishEndsAt()
        );
        return NoticeUpdateResponse.of(
            notice,
            toSeoulOffsetDateTime(notice.getPublishStartsAt()),
            toNullableSeoulOffsetDateTime(notice.getPublishEndsAt())
        );
    }

    @Transactional
    public void deleteNotice(String userId, Long noticeId) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        validateNoticeId(noticeId);
        Notice notice = findNoticeByIdOrThrow(noticeId);
        noticeRepository.delete(notice);
    }

    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    private Notice findNoticeByIdOrThrow(Long noticeId) {
        return noticeRepository.findById(noticeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공지를 찾을 수 없습니다."));
    }

    private void validateNoticeId(Long noticeId) {
        if (noticeId == null || noticeId < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "공지 ID가 올바르지 않습니다.");
        }
    }

    private void validateAdminUser(User user) {
        if (user.getUserStatus() != UserStatus.ACTIVE || user.getUserRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }
    }

    private void validateAdminNoticePageRequest(AdminNoticePageRequest request) {
        if (request == null || request.page() < 0 || request.size() < 1 || request.size() > AdminNoticePageRequest.MAX_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "페이지 요청 값이 올바르지 않습니다.");
        }
        if (!isAllStatus(request.status())) {
            parseStatus(request.status());
        }
    }

    private NoticeFields validateCreateRequest(NoticeCreateRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "공지 요청 값이 올바르지 않습니다.");
        }
        return new NoticeFields(
            validateTitle(request.title()),
            validateContent(request.content()),
            request.imgUrl(),
            parseStatus(request.status()),
            defaultPublishStartsAt(request.publishStartsAt()),
            toNullableSeoulLocalDateTime(request.publishEndsAt())
        );
    }

    private NoticeFields validateUpdateRequest(NoticeUpdateRequest request) {
        validateHasUpdatableField(request);

        String title = request.title() == null ? null : validateTitle(request.title());
        String content = request.content() == null ? null : validateContent(request.content());
        NoticeStatus status = request.status() == null ? null : parseStatus(request.status());
        LocalDateTime publishStartsAt = toNullableSeoulLocalDateTime(request.publishStartsAt());
        LocalDateTime publishEndsAt = toNullableSeoulLocalDateTime(request.publishEndsAt());

        return new NoticeFields(
            title,
            content,
            request.imgUrl(),
            status,
            publishStartsAt,
            publishEndsAt
        );
    }

    private void validateHasUpdatableField(NoticeUpdateRequest request) {
        if (request == null
            || (request.title() == null
                && request.content() == null
                && request.imgUrl() == null
                && request.status() == null
                && request.publishStartsAt() == null
                && request.publishEndsAt() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 공지 필드가 없습니다.");
        }
    }

    private String validateTitle(String title) {
        String trimmedTitle = trim(title);
        if (trimmedTitle.isEmpty() || trimmedTitle.length() > Notice.MAX_TITLE_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "공지 제목이 올바르지 않습니다.");
        }
        return trimmedTitle;
    }

    private String validateContent(String content) {
        String trimmedContent = trim(content);
        if (trimmedContent.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "공지 내용이 올바르지 않습니다.");
        }
        return trimmedContent;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private NoticeStatus parseStatus(String status) {
        try {
            return NoticeStatus.from(status);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    private boolean isAllStatus(String status) {
        return "all".equalsIgnoreCase(status);
    }

    private LocalDateTime defaultPublishStartsAt(OffsetDateTime publishStartsAt) {
        if (publishStartsAt == null) {
            return LocalDateTime.now(SEOUL_ZONE);
        }
        return toSeoulLocalDateTime(publishStartsAt);
    }

    private LocalDateTime toNullableSeoulLocalDateTime(OffsetDateTime dateTime) {
        return dateTime == null ? null : toSeoulLocalDateTime(dateTime);
    }

    private LocalDateTime toSeoulLocalDateTime(OffsetDateTime dateTime) {
        return dateTime.atZoneSameInstant(SEOUL_ZONE).toLocalDateTime();
    }

    private OffsetDateTime toSeoulOffsetDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(SEOUL_ZONE).toOffsetDateTime();
    }

    private OffsetDateTime toNullableSeoulOffsetDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : toSeoulOffsetDateTime(dateTime);
    }

    private boolean isNew(Notice notice) {
        LocalDateTime newBadgeEndsAt = notice.getPublishStartsAt().plusDays(NEW_BADGE_DAYS);
        return !LocalDateTime.now(SEOUL_ZONE).isAfter(newBadgeEndsAt);
    }

    private record NoticeFields(
        String title,
        String content,
        String imgUrl,
        NoticeStatus status,
        LocalDateTime publishStartsAt,
        LocalDateTime publishEndsAt
    ) {
    }
}
