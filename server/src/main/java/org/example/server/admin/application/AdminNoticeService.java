package org.example.server.admin.application;

import lombok.RequiredArgsConstructor;
import org.example.server.admin.presentation.dto.req.NoticeCreateRequest;
import org.example.server.admin.presentation.dto.req.NoticeUpdateRequest;
import org.example.server.admin.presentation.dto.res.NoticeCreateResponse;
import org.example.server.admin.presentation.dto.res.NoticeUpdateResponse;
import org.example.server.common.exception.AtchagongException;
import org.example.server.common.exception.ErrorCode;
import org.example.server.notice.domain.models.Notice;
import org.example.server.notice.domain.repository.NoticeRepository;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private static final int MAX_TITLE_LENGTH = 100;

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoticeCreateResponse createNotice(String userId, NoticeCreateRequest request) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        String title = validateTitle(request == null ? null : request.title());
        String content = validateContent(request == null ? null : request.content());

        Notice notice = Notice.create(admin.getId(), title, content);
        Notice savedNotice = noticeRepository.save(notice);
        return NoticeCreateResponse.from(savedNotice);
    }

    @Transactional
    public NoticeUpdateResponse updateNotice(String userId, Long noticeId, NoticeUpdateRequest request) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        validateNoticeId(noticeId);
        validateHasUpdatableField(request);

        String title = null;
        String content = null;
        if (request.title() != null) {
            title = validateTitle(request.title());
        }
        if (request.content() != null) {
            content = validateContent(request.content());
        }

        Notice notice = findNoticeByIdOrThrow(noticeId);
        notice.update(title, content);
        return NoticeUpdateResponse.from(notice);
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
            .orElseThrow(() -> new AtchagongException(ErrorCode.USER_NOT_FOUND));
    }

    private Notice findNoticeByIdOrThrow(Long noticeId) {
        return noticeRepository.findById(noticeId)
            .orElseThrow(() -> new AtchagongException(ErrorCode.NOTICE_NOT_FOUND));
    }

    private void validateNoticeId(Long noticeId) {
        if (noticeId == null || noticeId < 1) {
            throw new AtchagongException(ErrorCode.INVALID_NOTICE_ID);
        }
    }

    private void validateAdminUser(User user) {
        if (user.getUserStatus() != UserStatus.ACTIVE || user.getUserRole() != UserRole.ADMIN) {
            throw new AtchagongException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateHasUpdatableField(NoticeUpdateRequest request) {
        if (request == null || (request.title() == null && request.content() == null)) {
            throw new AtchagongException(ErrorCode.INVALID_REQUEST);
        }
    }

    private String validateTitle(String title) {
        String trimmedTitle = trim(title);
        if (trimmedTitle.isEmpty() || trimmedTitle.length() > MAX_TITLE_LENGTH) {
            throw new AtchagongException(ErrorCode.INVALID_NOTICE_TITLE);
        }
        return trimmedTitle;
    }

    private String validateContent(String content) {
        String trimmedContent = trim(content);
        if (trimmedContent.isEmpty()) {
            throw new AtchagongException(ErrorCode.INVALID_NOTICE_CONTENT);
        }
        return trimmedContent;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
