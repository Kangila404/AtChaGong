package org.example.server.attendance.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AttendanceErrorCode implements ErrorCode {
    ALREADY_ATTENDED(HttpStatus.CONFLICT, "오늘은 이미 출석했습니다.");

    private final HttpStatus status;
    private final String message;
}
