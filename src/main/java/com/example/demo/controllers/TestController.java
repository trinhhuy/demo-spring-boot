package com.example.demo.controllers;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.MDC;

import com.example.demo.services.App1Service;
import com.example.demo.services.AsyncApp1Service;
import com.example.demo.services.ReactiveApp1Service;
import com.example.demo.services.LoggingService;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    private LoggingService loggingService;

    @Autowired
    private App1Service app1Service;

    @Autowired
    private ReactiveApp1Service reactiveApp1Service;

    @Autowired
    private AsyncApp1Service asyncApp1Service;

    @PostMapping("/sync")
    public String sync() {
//        log.info("Starting sync call to app1");
        String response = app1Service.getHelloFromApp1();
//        log.info("Completed sync call to app1 with response: {}", response);
        return "Response from app1: " + response;
    }

    @GetMapping("/call-app1-async")
    public CompletableFuture<String> callApp1Async() {
        String traceId = MDC.get("traceId");
        loggingService.logInfo("before calling");

        return asyncApp1Service.callApp1Async()
                .thenApply(response -> {
                    try {
                        MDC.put("traceId", traceId);
                        loggingService.logInfo("Received async response xxxxxxxxxx---");
                        return "Async response from app1: " + response;
                    } finally {
                        MDC.clear();
                    }
                });
    }

    @GetMapping("/fire-forget")
    public String fireAndForget() {
        // Start the async task but don't wait for it
        // asyncApp1Service.fireAndForget();
        reactiveApp1Service.fireAndForgetReactive();
        // Return immediately
        return "Request initiated, not waiting for result";
    }

    // @GetMapping("/test")
    // public String test() {
    //     logger.debug("Debug log message");
    //     logger.info("Info log message");
    //     logger.warn("Warning log message");
    //     logger.error("Error log message");
    //     return "Test logging";
    // }
}
