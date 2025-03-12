package com.example.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    public void log(String message, Object data) {
        String traceId = MDC.get("traceId");
        String controller = MDC.get("controller");
        String function = MDC.get("function");

        MDC.put("message", message);
        MDC.put("data", data != null ? data.toString() : "null");

        logger.info("Log: traceId={}, controller={}, function={}, message={}, data={}",
                traceId, controller, function, message, data);

        MDC.remove("message");
        MDC.remove("data");
    }
}
