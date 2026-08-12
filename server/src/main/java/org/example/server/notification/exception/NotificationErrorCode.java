package org.example.server.notification.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    INVALID_NOTIFICATION_SETTING(HttpStatus.BAD_REQUEST, "알림 설정이 올바르지 않습니다."),
    INVALID_DEVICE_TOKEN(HttpStatus.BAD_REQUEST, "기기 토큰이 올바르지 않습니다."),
    INVALID_PLATFORM(HttpStatus.BAD_REQUEST, "플랫폼이 올바르지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
