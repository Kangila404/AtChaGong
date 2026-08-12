package org.example.server.notification.exception;

import org.example.server.common.exception.AtchagongException;

public class NotificationException extends AtchagongException {
    public NotificationException(NotificationErrorCode errorCode) {
        super(errorCode);
    }
}
