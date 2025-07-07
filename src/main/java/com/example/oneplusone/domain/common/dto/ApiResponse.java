package com.example.oneplusone.domain.common.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timeStamp;

    @Builder(access = AccessLevel.PRIVATE)
    private ApiResponse(boolean success, String message, T data, LocalDateTime timeStamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timeStamp = timeStamp;
    }

    // 성공 응답
    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
