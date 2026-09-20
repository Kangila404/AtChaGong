package org.example.server.coin.exception;

import org.example.server.common.exception.AtchagongException;

public class CoinException extends AtchagongException {
    public CoinException(CoinErrorCode errorCode) {
        super(errorCode);
    }
}
