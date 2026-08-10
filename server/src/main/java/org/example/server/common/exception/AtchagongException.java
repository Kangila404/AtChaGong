package org.example.server.common.exception;

import lombok.Getter;

@Getter
public class AtchagongException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String code;
    private final String message;

    public AtchagongException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }
}
