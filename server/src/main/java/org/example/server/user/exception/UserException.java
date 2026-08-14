package org.example.server.user.exception;

import org.example.server.common.exception.AtchagongException;

public class UserException extends AtchagongException {


    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}
