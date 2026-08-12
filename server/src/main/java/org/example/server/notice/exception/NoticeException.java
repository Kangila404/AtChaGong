package org.example.server.notice.exception;

import org.example.server.common.exception.AtchagongException;

public class NoticeException extends AtchagongException {
    public NoticeException(NoticeErrorCode errorCode) {
        super(errorCode);
    }
}
