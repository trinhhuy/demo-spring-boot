package com.example.demo.exception;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.dto.response.ResponseUtils;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String ISSUE = "issue";
    private static final String FIELD = "field";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        // get errors from BindingResult
        List<Object> details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(FIELD, error.getField(), ISSUE, error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseUtils.error(ErrorCode.VALIDATION_FAILED, details);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse<Object>> handleAccessDeniedException(AccessDeniedException exception) {
        return createErrorResponse(exception.getMessage(), ErrorCode.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse<Object>> handleAccessDeniedException(AuthenticationException exception) {
        return createErrorResponse(exception.getMessage(), ErrorCode.UNAUTHORIZED);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse<Object>> handleAppException(AppException appException) {
        return ResponseUtils.error(appException);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<ErrorResponse<Object>> handleSocketTimeoutException(SocketTimeoutException exception) {
        return createErrorResponse(exception.getMessage(), ErrorCode.SOCKET_TIMEOUT_EXCEPTION);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse<Object>> handleResourceAccessException(ResourceAccessException exception) {
        return createErrorResponse(exception.getMessage(), ErrorCode.RESOURCE_ACCESS_EXCEPTION);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse<Object>> handleRuntimeException(RuntimeException exception) {
        return createErrorResponse(exception.getMessage(), ErrorCode.UNCATEGORIZED_EXCEPTION);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<Object>> handleException(Exception exception) {
        return createErrorResponse(exception.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse<Object>> createErrorResponse(String issue, ErrorCode errorCode) {
        List<Object> details = new ArrayList<>();
        details.add(Map.of(ISSUE, issue));
        return ResponseUtils.error(errorCode, details);
    }
}
