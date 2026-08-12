package org.example.server.statistics.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum StatisticsErrorCode implements ErrorCode {
    INVALID_PERIOD(HttpStatus.BAD_REQUEST, "잘못된 기간입니다."),
    INVALID_YEAR_MONTH(HttpStatus.BAD_REQUEST, "잘못된 연도 또는 월입니다.");

    private final HttpStatus status;
    private final String message;
}