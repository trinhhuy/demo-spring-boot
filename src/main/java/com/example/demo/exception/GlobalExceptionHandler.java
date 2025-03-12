package com.example.demo.exception;

import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.dto.response.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.info("handleMethodArgumentNotValidException");
        // get errors from BindingResult
        BindingResult result = ex.getBindingResult();
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : result.getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse<Map<String, String>> errorResponse = new ErrorResponse<>();
        errorResponse.setCode(ErrorCode.INVALID_DTO.getCode());
        errorResponse.setMessage(ErrorCode.INVALID_DTO.getMessage());
        errorResponse.setErrors(fieldErrors);
        return ResponseEntity.status(ErrorCode.INVALID_DTO.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse<Void>> handleAppException(AppException ex) {
        log.info("Handling AppException: {}", ex.getMessage());
        return ResponseUtils.error(ex);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse<Void>> handleRuntimeException(RuntimeException ex) {
        log.info("Handling RuntimeException: {}", ex.getMessage());
        return ResponseUtils.error(new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<Void>> handleException(Exception ex) {
        log.info("Handling Exception: {}",  ex.getMessage());
        return ResponseUtils.error(new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
    }

}
