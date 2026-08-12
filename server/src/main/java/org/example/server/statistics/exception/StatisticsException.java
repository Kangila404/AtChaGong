package org.example.server.statistics.exception;

import org.example.server.common.exception.AtchagongException;

public class StatisticsException extends AtchagongException {

    public StatisticsException(StatisticsErrorCode errorCode) {
        super(errorCode);
    }
}
