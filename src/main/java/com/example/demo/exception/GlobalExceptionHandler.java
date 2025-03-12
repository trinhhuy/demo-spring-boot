package com.example.demo.exception;

import com.example.demo.dto.response.AppResponse;
import com.example.demo.dto.response.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<AppResponse<Void>> handleAppException(AppException ex) {
        log.info("Handling AppException: {}", ex.getMessage());
        return ResponseUtils.error(ex);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AppResponse<Void>> handleRuntimeException(RuntimeException ex) {
        log.info("Handling RuntimeException: {}", ex.getMessage());
        return ResponseUtils.error(new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppResponse<Void>> handleException(Exception ex) {
        log.info("Handling Exception: {}",  ex.getMessage());
        return ResponseUtils.error(new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
    }

}
