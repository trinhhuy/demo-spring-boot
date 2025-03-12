package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.demo.services.LoggingService;

@Aspect
@Component
public class LoggingAspect {

    @Autowired
    private LoggingService loggingService;

    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controller() {}

    @Before("controller()")
    public void logBefore(JoinPoint joinPoint) {
        String traceId = generateTraceId(); // Implement this method to generate a unique traceId
        String controllerName = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        MDC.put("traceId", traceId);
        MDC.put("controller", controllerName);
        MDC.put("function", methodName);

        loggingService.log("Entering method", joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "controller()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        loggingService.log("Exiting method", result);

        // Clear MDC after request is complete
        MDC.clear();
    }

    private String generateTraceId() {
        // Implement logic to generate a unique traceId
        return java.util.UUID.randomUUID().toString();
    }
}
