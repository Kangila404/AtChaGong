package org.example.server.common.response;

import org.example.server.common.exception.ErrorResponse;

public record ApiResponse<T>(
    boolean success,
    T data,
    String message,
    ErrorResponse error
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null);
    }

    public static <T> ApiResponse<T> failure(ErrorResponse error) {
        return new ApiResponse<>(false, null, null, error);
    }
}