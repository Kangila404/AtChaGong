package org.example.server.timer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TimerErrorCode implements ErrorCode {
    INVALID_FOCUS_MINUTES(HttpStatus.BAD_REQUEST, "집중 시간은 1분 이상이어야 합니다."),
    INVALID_BREAK_MINUTES(HttpStatus.BAD_REQUEST, "휴식 시간은 1분 이상이어야 합니다."),
    INVALID_CYCLE_COUNT(HttpStatus.BAD_REQUEST, "반복 횟수는 1회 이상이어야 합니다."),
    TIMER_NOT_FOUND(HttpStatus.NOT_FOUND, "타이머 설정을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}