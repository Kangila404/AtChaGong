package org.example.server.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "유효하지 않은 닉네임입니다."),
    ALREADY_WITHDRAWN_USER(HttpStatus.FORBIDDEN, "이미 탈퇴한 회원입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    SUSPENDED_USER(HttpStatus.FORBIDDEN, "정지된 회원입니다."),
    WITHDRAWN_USER(HttpStatus.FORBIDDEN, "탈퇴한 회원입니다.");

    private final HttpStatus status;
    private final String message;
}