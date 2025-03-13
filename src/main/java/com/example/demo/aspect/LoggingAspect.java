package com.example.demo.aspect;

import com.example.demo.config.TraceIdConfig;
import com.example.demo.services.LoggingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Aspect
@Component
public class LoggingAspect {
    private final LoggingService loggingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoggingAspect(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Around("execution(* com.example.demo.controllers..*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            String className = joinPoint.getSignature().getDeclaringTypeName();
            String methodName = joinPoint.getSignature().getName();

            // Thêm controller và method name vào MDC
            MDC.put("controller", className);
            MDC.put("method", methodName);

            // Log request
            Object[] args = joinPoint.getArgs();
            String requestData = args.length > 0 ? objectMapper.writeValueAsString(args) : "no data";
            loggingService.log("Enter: {}.{}", className, methodName, requestData);

            Object result = joinPoint.proceed();

            // Log response cho các phương thức đồng bộ
            String responseData = result != null ? objectMapper.writeValueAsString(result) : "void";
            loggingService.log("Exit: {}.{}", className, methodName, responseData);
            
            return result;
        } finally {
            MDC.clear();
        }
    }
}
