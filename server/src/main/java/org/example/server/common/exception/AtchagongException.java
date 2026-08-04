package org.example.server.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AtchagongException extends RuntimeException {
    private final HttpStatus status;
    private final String message;


    public AtchagongException(HttpStatus status, String message)
    {
        super(message);
        this.status = status;
        this.message = message;
    }
}
