package org.example.server.beverage.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BeverageErrorCode implements ErrorCode {
    BEVERAGE_ID_REQUIRED(HttpStatus.BAD_REQUEST, "beverageId는 필수입니다."),
    BEVERAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 음료입니다.");

    private final HttpStatus status;
    private final String message;
}