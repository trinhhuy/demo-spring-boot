package com.example.demo.exception;

import com.example.demo.dto.response.ApiResponseDto;
import com.example.demo.enums.ResponseCode;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGrpcException(StatusRuntimeException ex) {
        log.error("gRPC exception: {}", ex.getMessage());
        Status.Code code = ex.getStatus().getCode();
        String message = ex.getStatus().getDescription() != null
                ? ex.getStatus().getDescription()
                : ex.getMessage();

        ResponseCode httpStatus = switch (code) {
            case NOT_FOUND -> ResponseCode.NOT_FOUND;
            case INVALID_ARGUMENT -> ResponseCode.BAD_REQUEST;
            case UNAVAILABLE -> ResponseCode.SERVICE_UNAVAILABLE;
            case PERMISSION_DENIED -> ResponseCode.FORBIDDEN;
            case DEADLINE_EXCEEDED -> ResponseCode.GATEWAY_TIMEOUT;
            case ALREADY_EXISTS -> ResponseCode.CONFLICT;
            default -> ResponseCode.INTERNAL_SERVER_ERROR;
        };


        return ApiResponseDto.error(httpStatus, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException: {}", exception.getMessage(), exception);
        // get errors from BindingResult
        String msg = exception.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.joining(", "));

        return ApiResponseDto.error(ResponseCode.BAD_REQUEST, msg);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleException(Exception exception) {
        log.error("Exception: {}", exception.getMessage(), exception);
        return ApiResponseDto.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }
}

