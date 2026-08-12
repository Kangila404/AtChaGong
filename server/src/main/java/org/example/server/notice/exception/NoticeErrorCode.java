package org.example.server.notice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NoticeErrorCode implements ErrorCode {
    INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST, "페이지 요청 값이 올바르지 않습니다."),
    INVALID_NOTICE_STATUS(HttpStatus.BAD_REQUEST, "공지 상태가 올바르지 않습니다."),
    INVALID_NOTICE_ID(HttpStatus.BAD_REQUEST, "공지 ID가 올바르지 않습니다."),
    INVALID_NOTICE_TITLE(HttpStatus.BAD_REQUEST, "공지 제목이 올바르지 않습니다."),
    INVALID_NOTICE_CONTENT(HttpStatus.BAD_REQUEST, "공지 내용이 올바르지 않습니다."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "공지를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
