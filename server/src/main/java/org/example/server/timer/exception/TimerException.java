package org.example.server.timer.exception;

import org.example.server.common.exception.AtchagongException;

public class TimerException extends AtchagongException {
    public TimerException(TimerErrorCode errorCode) {
        super(errorCode);
    }
}