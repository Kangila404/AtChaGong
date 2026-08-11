package org.example.server.auth.exception;

import org.example.server.common.exception.AtchagongException;

public class AuthException extends AtchagongException {
    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(AuthErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}