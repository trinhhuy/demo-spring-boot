package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void logEnter(String className, String methodName, Object[] args) {
        try {
            String requestData = args.length > 0 ? objectMapper.writeValueAsString(args) : "no data";
            MDC.put("data", requestData);
            logger.info("Enter: {}.{}", className, methodName);
        } catch (Exception e) {
            logger.error("Error logging enter: ", e);
        }
    }

    public void logExit(String className, String methodName, Object result) {
        try {
            logger.info("Exit: {}.{} with result: {}", className, methodName,
                    result != null ? objectMapper.writeValueAsString(result) : "void");
        } catch (Exception e) {
            logger.error("Error logging exit: ", e);
        }
    }
} 