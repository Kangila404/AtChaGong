package org.example.server.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AtchagongException.class)
    public ResponseEntity<ErrorResponse> handleAtchagongException(AtchagongException e) {
        return ResponseEntity
            .status(e.getStatus())
            .body(ErrorResponse.of(e.getStatus().value(), e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        return ResponseEntity
            .status(CommonErrorCode.INVALID_REQUEST.getStatus())
            .body(ErrorResponse.of(CommonErrorCode.INVALID_REQUEST));
    }
}