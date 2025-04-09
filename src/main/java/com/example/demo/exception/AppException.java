package com.example.demo.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;
    private final List<Object> details; // Có thể là field error, hệ thống lỗi, etc.

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = new ArrayList<>(); // Mặc định không có chi tiết
    }

    public AppException(ErrorCode errorCode, List<Object> details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details != null ? details : new ArrayList<>();
    }

    public HttpStatusCode getCode() {
        return errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }

    // Giúp dễ dàng thêm details mà không cần truyền trực tiếp List<Object>
    public AppException withDetails(Object... details) {
        this.details.addAll(Arrays.asList(details));
        return this;
    }
}
