package org.example.server.beverage.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BeverageErrorCode implements ErrorCode {
    BEVERAGE_ID_REQUIRED(HttpStatus.BAD_REQUEST, "beverageId는 필수입니다."),
    BEVERAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 음료입니다."),
    BEVERAGE_NOT_OWNED(HttpStatus.FORBIDDEN, "보유하지 않은 음료입니다."),
    BEVERAGE_NOT_ON_SALE(HttpStatus.CONFLICT, "현재 판매 중인 음료가 아닙니다."),
    BEVERAGE_ALREADY_OWNED(HttpStatus.CONFLICT, "이미 보유한 음료입니다."),
    INVALID_BEVERAGE_ID(HttpStatus.BAD_REQUEST, "음료 ID가 올바르지 않습니다."),
    INVALID_BEVERAGE_NAME(HttpStatus.BAD_REQUEST, "음료 이름이 올바르지 않습니다."),
    INVALID_BEVERAGE_PRICE(HttpStatus.BAD_REQUEST, "음료 가격이 올바르지 않습니다."),
    INVALID_BEVERAGE_IMAGE_URL(HttpStatus.BAD_REQUEST, "음료 이미지 URL이 올바르지 않습니다."),
    INVALID_BEVERAGE_SALE_STATUS(HttpStatus.BAD_REQUEST, "음료 판매 상태가 올바르지 않습니다."),
    INVALID_BEVERAGE_SALE_ENDS_AT(HttpStatus.BAD_REQUEST, "음료 판매 종료 시각이 올바르지 않습니다."),
    INVALID_BEVERAGE_DISPLAY_ORDER(HttpStatus.BAD_REQUEST, "음료 진열 순서가 올바르지 않습니다."),
    INVALID_BEVERAGE_PAGE_REQUEST(HttpStatus.BAD_REQUEST, "음료 목록 조회 요청이 올바르지 않습니다."),
    INVALID_BEVERAGE_ORDER_REQUEST(HttpStatus.BAD_REQUEST, "음료 순서 변경 요청이 올바르지 않습니다."),
    DEFAULT_BEVERAGE_MODIFICATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "기본 음료에는 요청한 변경을 적용할 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
