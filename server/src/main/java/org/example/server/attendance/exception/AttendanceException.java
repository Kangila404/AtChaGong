package org.example.server.attendance.exception;

import org.example.server.common.exception.AtchagongException;

public class AttendanceException extends AtchagongException {
    public AttendanceException(AttendanceErrorCode errorCode) {
        super(errorCode);
    }
}
