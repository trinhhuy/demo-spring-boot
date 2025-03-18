package com.example.demo.dto.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.exception.AppException;

public class ResponseUtils {
    private void ResponseUtil() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static <T> ResponseEntity<AppResponse<T>> success(T data) {
        AppResponse<T> response = new AppResponse<>();
        response.setResult(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static ResponseEntity<AppResponse<Void>> success() {
        AppResponse<Void> response = new AppResponse<>();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static ResponseEntity<AppResponse<Void>> success(String message) {
        AppResponse<Void> response = new AppResponse<>();
        response.setMessage(message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<AppResponse<T>> successWithHeaders(T data, HttpHeaders headers) {
        AppResponse<T> response = new AppResponse<>();
        response.setResult(data);
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    public static <T> ResponseEntity<AppResponse<T>> created(T data) {
        AppResponse<T> response = new AppResponse<>();
        response.setResult(data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public static ResponseEntity<AppResponse<Void>> created(String message) {
        AppResponse<Void> response = new AppResponse<>();
        response.setMessage(message);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public static ResponseEntity<Void> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //    public static <T> ResponseEntity<AppResponse<T>> error(AppException ex) {
    //        AppResponse<T> response = new AppResponse<>();
    //        response.setCode(ex.getErrorCode().getCode());
    //        response.setMessage(ex.getErrorCode().getMessage());
    //        return new ResponseEntity<>(response, ex.getErrorCode().getStatusCode());
    //    }

    public static <T> ResponseEntity<ErrorResponse<T>> error(AppException ex) {
        ErrorResponse<T> response = new ErrorResponse<>();
        response.setCode(ex.getErrorCode().getCode());
        response.setMessage(ex.getErrorCode().getMessage());
        return new ResponseEntity<>(response, ex.getErrorCode().getStatusCode());
    }
}
