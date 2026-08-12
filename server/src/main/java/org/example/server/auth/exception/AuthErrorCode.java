package org.example.server.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    PROVIDER_TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "providerToken이 없습니다."),
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST,    "지원하지 않는 provider 타입입니다."),
    INVALID_PROVIDER_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 provider 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 리프레시 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    REFRESH_TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "폐기된 리프레시 토큰입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 토큰에 대한 유저를 찾을 수 없습니다."),

    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Access Token입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 Access Token입니다.");

    private final HttpStatus status;
    private final String message;

}