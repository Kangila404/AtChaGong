package org.example.server.beverage.exception;

import org.example.server.common.exception.AtchagongException;

public class BeverageException extends AtchagongException {
    public BeverageException(BeverageErrorCode errorCode) {
        super(errorCode);
    }
}