package org.example.server.coin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CoinErrorCode implements ErrorCode {
    COIN_BALANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "코인 지갑을 찾을 수 없습니다."),
    INSUFFICIENT_COIN(HttpStatus.CONFLICT, "보유 코인이 부족합니다."),
    DUPLICATE_COIN_TRANSACTION(HttpStatus.CONFLICT, "이미 처리된 코인 거래입니다.");

    private final HttpStatus status;
    private final String message;
}
