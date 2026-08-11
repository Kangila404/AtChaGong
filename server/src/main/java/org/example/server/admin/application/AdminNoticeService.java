package org.example.server.admin.application;

import lombok.RequiredArgsConstructor;
import org.example.server.admin.presentation.dto.req.NoticeCreateRequest;
import org.example.server.admin.presentation.dto.req.NoticeUpdateRequest;
import org.example.server.admin.presentation.dto.res.NoticeCreateResponse;
import org.example.server.admin.presentation.dto.res.NoticeUpdateResponse;
import org.example.server.notice.domain.models.Notice;
import org.example.server.notice.domain.repository.NoticeRepository;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoticeCreateResponse createNotice(String userId, NoticeCreateRequest request) {
        User admin = findUserByUserIdOrThrow(userId);
        validateAdminUser(admin);
        NoticeFields fields = validateCreateRequest(request);

        Notice notice = Notice.create(admin.getId(), fields.title(), fields.content());
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
        notice.update(fields.title(), fields.content());
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

    private NoticeFields validateCreateRequest(NoticeCreateRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "공지 요청 값이 올바르지 않습니다.");
        }
        return new NoticeFields(
            validateTitle(request.title()),
            validateContent(request.content())
        );
    }

    private NoticeFields validateUpdateRequest(NoticeUpdateRequest request) {
        validateHasUpdatableField(request);

        String title = request.title() == null ? null : validateTitle(request.title());
        String content = request.content() == null ? null : validateContent(request.content());

        return new NoticeFields(title, content);
    }

    private void validateHasUpdatableField(NoticeUpdateRequest request) {
        if (request == null || (request.title() == null && request.content() == null)) {
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

    private record NoticeFields(
        String title,
        String content
    ) {
    }
}
