package com.example.demo.exception;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GlobalHandlerExceptionGRPC {
    @GrpcExceptionHandler(StatusRuntimeException.class)
    public StatusException handleStatusRuntimeException(StatusRuntimeException e) {
        System.out.println("\n\n------ handleStatusRuntimeException");
        return e.getStatus().asException();
    }

    @GrpcExceptionHandler(Exception.class)
    public StatusException handleException(Exception e) {
        System.out.println("\n\n------ handleException");
        System.out.println(e.getMessage());
        return Status.INTERNAL.withCause(e).asException();
    }
}
