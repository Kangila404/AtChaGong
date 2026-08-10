package org.example.server.common.exception;

import org.example.server.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AtchagongException.class)
    public ResponseEntity<ApiResponse<Void>> handleAtchagongException(AtchagongException exception) {
        ErrorResponse errorResponse = ErrorResponse.of(
            exception.getErrorCode().getStatus().value(),
            exception.getCode(),
            exception.getMessage()
        );
        return ResponseEntity.status(exception.getErrorCode().getStatus()).body(ApiResponse.failure(errorResponse));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        ErrorCode errorCode = getRequestValueErrorCode(exception.getName());
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.failure(ErrorResponse.of(errorCode)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
        MissingServletRequestParameterException exception
    ) {
        ErrorCode errorCode = getRequestValueErrorCode(exception.getParameterName());
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.failure(ErrorResponse.of(errorCode)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException() {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.failure(ErrorResponse.of(errorCode)));
    }

    private ErrorCode getRequestValueErrorCode(String name) {
        if ("noticeId".equals(name)) {
            return ErrorCode.INVALID_NOTICE_ID;
        }
        if ("page".equals(name) || "size".equals(name)) {
            return ErrorCode.INVALID_PAGE_REQUEST;
        }
        return ErrorCode.INVALID_REQUEST;
    }
}
