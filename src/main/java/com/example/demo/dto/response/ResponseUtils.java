package com.example.demo.dto.response;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;

public class ResponseUtils {
    private void ResponseUtil() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    // success response
    public static <T> ResponseEntity<AppResponse<T>> success(T data) {
        AppResponse<T> response = new AppResponse<>();
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<AppResponse<T>> success(AppResponse<T> response) {
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static ResponseEntity<AppResponse<Void>> success() {
        AppResponse<Void> response = new AppResponse<>();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<AppResponse<T>> success(T data, Map<String, Object> metadata) {
        AppResponse<T> response = new AppResponse<>();
        response.setData(data);
        response.setMetadata(metadata);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<AppResponse<T>> successWithHeaders(T data, HttpHeaders headers) {
        AppResponse<T> response = new AppResponse<>();
        response.setData(data);
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    public static <T> ResponseEntity<AppResponse<T>> created(T data) {
        AppResponse<T> response = new AppResponse<>();
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public static ResponseEntity<AppResponse<Void>> created(Map<String, Object> metadata) {
        AppResponse<Void> response = new AppResponse<>();
        response.setMetadata(metadata);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public static ResponseEntity<Void> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // error response
    public static ResponseEntity<ErrorResponse<Object>> error(AppException appException) {
        return ResponseEntity.status(appException.getCode()).body(createErrorResponse(appException));
    }

    public static ResponseEntity<ErrorResponse<Object>> error(ErrorCode errorCode, List<Object> details) {
        return ResponseEntity.status(errorCode.getCode()).body(createErrorResponse(errorCode, details));
    }

    private static ErrorResponse<Object> createErrorResponse(AppException appException) {
        ErrorDetail<Object> errorDetail = ErrorDetail.builder()
                .code(appException.getCode().value())
                .message(appException.getMessage())
                .details(appException.getDetails())
                .build();
        return ErrorResponse.builder().error(errorDetail).build();
    }

    private static ErrorResponse<Object> createErrorResponse(ErrorCode errorCode, List<Object> details) {
        ErrorDetail<Object> errorDetail = ErrorDetail.builder()
                .code(errorCode.getCode().value())
                .message(errorCode.getMessage())
                .details(details)
                .build();
        return ErrorResponse.builder().error(errorDetail).build();
    }
}
