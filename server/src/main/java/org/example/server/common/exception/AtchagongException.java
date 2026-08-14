package org.example.server.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AtchagongException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final String message;

    public AtchagongException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public AtchagongException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.status = errorCode.getStatus();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }
}