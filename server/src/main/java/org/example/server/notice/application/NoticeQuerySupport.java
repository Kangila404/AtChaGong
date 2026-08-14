package org.example.server.notice.application;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.example.server.notice.domain.models.Notice;
import org.example.server.notice.exception.NoticeErrorCode;
import org.example.server.notice.exception.NoticeException;
import org.springframework.stereotype.Component;

@Component
public class NoticeQuerySupport {

    public static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final int NEW_BADGE_DAYS = 7;

    public Long parseNoticeId(String noticeId) {
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

    public int parsePageValue(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new NoticeException(NoticeErrorCode.INVALID_PAGE_REQUEST);
        }
    }

    public OffsetDateTime toSeoulOffsetDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(SEOUL_ZONE).toOffsetDateTime();
    }

    public boolean isNew(Notice notice) {
        LocalDateTime now = LocalDateTime.now(SEOUL_ZONE);
        LocalDateTime publishStartsAt = notice.getPublishStartsAt();
        LocalDateTime newBadgeEndsAt = publishStartsAt.plusDays(NEW_BADGE_DAYS);
        return !now.isBefore(publishStartsAt) && !now.isAfter(newBadgeEndsAt);
    }
}