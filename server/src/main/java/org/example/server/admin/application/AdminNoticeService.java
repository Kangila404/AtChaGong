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
import org.example.server.common.exception.AtchagongException;
import org.example.server.common.exception.CommonErrorCode;
import org.example.server.notice.domain.enums.NoticeStatus;
import org.example.server.notice.domain.models.Notice;
import org.example.server.notice.domain.repository.NoticeRepository;
import org.example.server.notice.exception.NoticeErrorCode;
import org.example.server.notice.exception.NoticeException;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final int NEW_BADGE_DAYS = 7;
    private static final int MAX_IMG_URL_LENGTH = 512;

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AdminNoticePageResponse getNotices(String userId, AdminNoticePageRequest request) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        PageValues pageValues = parseAdminNoticePageRequest(request);

        PageRequest pageRequest = PageRequest.of(
            pageValues.page(),
            pageValues.size(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<Notice> noticePage = pageValues.status() == null
            ? noticeRepository.findAll(pageRequest)
            : noticeRepository.findByStatus(pageValues.status(), pageRequest);

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
    public AdminNoticeDetailResponse getNotice(String userId, String noticeId) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        Long parsedNoticeId = parseNoticeId(noticeId);

        Notice notice = findNoticeByIdOrThrow(parsedNoticeId);
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
    public NoticeUpdateResponse updateNotice(String userId, String noticeId, NoticeUpdateRequest request) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        Long parsedNoticeId = parseNoticeId(noticeId);
        NoticeFields fields = validateUpdateRequest(request);

        Notice notice = findNoticeByIdOrThrow(parsedNoticeId);
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
    public void deleteNotice(String userId, String noticeId) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        Long parsedNoticeId = parseNoticeId(noticeId);
        Notice notice = findNoticeByIdOrThrow(parsedNoticeId);
        noticeRepository.delete(notice);
    }

    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Notice findNoticeByIdOrThrow(Long noticeId) {
        return noticeRepository.findById(noticeId)
            .orElseThrow(() -> new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND));
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

    private void validateAdminUser(User user) {
        if (user.getUserStatus() != UserStatus.ACTIVE || user.getUserRole() != UserRole.ADMIN) {
            throw new AtchagongException(CommonErrorCode.FORBIDDEN);
        }
    }

    private PageValues parseAdminNoticePageRequest(AdminNoticePageRequest request) {
        if (request == null) {
            throw new NoticeException(NoticeErrorCode.INVALID_PAGE_REQUEST);
        }

        int page = parsePageValue(request.page(), AdminNoticePageRequest.DEFAULT_PAGE);
        int size = parsePageValue(request.size(), AdminNoticePageRequest.DEFAULT_SIZE);
        if (page < 0 || size < 1 || size > AdminNoticePageRequest.MAX_SIZE) {
            throw new NoticeException(NoticeErrorCode.INVALID_PAGE_REQUEST);
        }

        NoticeStatus status = isAllStatus(request.status()) ? null : parseStatus(request.status());
        return new PageValues(page, size, status);
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

    private NoticeFields validateCreateRequest(NoticeCreateRequest request) {
        if (request == null) {
            throw new AtchagongException(CommonErrorCode.INVALID_REQUEST);
        }
        return new NoticeFields(
            validateTitle(request.title()),
            validateContent(request.content()),
            validateImgUrl(request.imgUrl()),
            parseStatus(request.status()),
            defaultPublishStartsAt(request.publishStartsAt()),
            toNullableSeoulLocalDateTime(request.publishEndsAt())
        );
    }

    private NoticeFields validateUpdateRequest(NoticeUpdateRequest request) {
        validateHasUpdatableField(request);

        String title = request.title() == null ? null : validateTitle(request.title());
        String content = request.content() == null ? null : validateContent(request.content());
        String imgUrl = request.imgUrl() == null ? null : validateImgUrl(request.imgUrl());
        NoticeStatus status = request.status() == null ? null : parseStatus(request.status());
        LocalDateTime publishStartsAt = toNullableSeoulLocalDateTime(request.publishStartsAt());
        LocalDateTime publishEndsAt = toNullableSeoulLocalDateTime(request.publishEndsAt());

        return new NoticeFields(
            title,
            content,
            imgUrl,
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
            throw new AtchagongException(CommonErrorCode.INVALID_REQUEST);
        }
    }

    private String validateTitle(String title) {
        String trimmedTitle = trim(title);
        if (trimmedTitle.isEmpty() || trimmedTitle.length() > Notice.MAX_TITLE_LENGTH) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_TITLE);
        }
        return trimmedTitle;
    }

    private String validateContent(String content) {
        String trimmedContent = trim(content);
        if (trimmedContent.isEmpty()) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_CONTENT);
        }
        return trimmedContent;
    }

    private String validateImgUrl(String imgUrl) {
        if (imgUrl == null || imgUrl.length() <= MAX_IMG_URL_LENGTH) {
            return imgUrl;
        }
        throw new AtchagongException(CommonErrorCode.INVALID_REQUEST);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private NoticeStatus parseStatus(String status) {
        try {
            return NoticeStatus.from(status);
        } catch (IllegalArgumentException exception) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_STATUS);
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

    private record PageValues(int page, int size, NoticeStatus status) {
    }
}
