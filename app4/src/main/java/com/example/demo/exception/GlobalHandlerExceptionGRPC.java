package com.example.demo.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GlobalHandlerExceptionGRPC {
    @GrpcExceptionHandler(AppException.class)
    public StatusRuntimeException handleAppException(AppException e) {
        return e.getErrorCode().getGrpcStatus().withDescription(e.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(StatusRuntimeException.class)
    public StatusRuntimeException handleStatusRuntimeException(StatusRuntimeException e) {
        return e.getStatus().asRuntimeException();
    }

    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleException(Exception e) {
        return Status.INTERNAL.withCause(e).asRuntimeException();
    }
}
