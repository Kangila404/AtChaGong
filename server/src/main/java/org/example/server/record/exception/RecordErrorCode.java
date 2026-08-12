package org.example.server.record.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecordErrorCode implements ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "집중 시간 범위가 올바르지 않습니다."),
    INVALID_FOCUS_MINUTES(HttpStatus.BAD_REQUEST, "집중 시간이 올바르지 않습니다."),
    INVALID_FOCUSED_SECONDS(HttpStatus.BAD_REQUEST, "실제 집중 시간이 올바르지 않습니다."),
    INCOMPLETE_FOCUS(HttpStatus.BAD_REQUEST, "완료되지 않은 집중 기록입니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "날짜가 올바르지 않습니다."),
    BEVERAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "음료를 찾을 수 없습니다."),
    DUPLICATE_FOCUS_RECORD(HttpStatus.CONFLICT, "이미 저장된 집중 기록입니다.");

    private final HttpStatus status;
    private final String message;
}