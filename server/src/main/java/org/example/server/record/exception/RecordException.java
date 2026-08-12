package org.example.server.record.exception;

import org.example.server.common.exception.AtchagongException;
import org.example.server.common.exception.ErrorCode;

public class RecordException extends AtchagongException {


    public RecordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
