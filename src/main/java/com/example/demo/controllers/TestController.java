package com.example.demo.controllers;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.services.App1Service;
import com.example.demo.services.AsyncApp1Service;
import com.example.demo.services.ReactiveApp1Service;
import com.example.demo.services.LoggingService;
import com.example.demo.services.ExampleService;
import com.example.demo.services.CircuitBreakerCustomService;
import com.example.demo.dto.CircuitBreakerTestRequest;
import com.example.demo.dto.ApiResponse;

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

    private final ExampleService exampleService;
    private final CircuitBreakerCustomService circuitBreakerCustomService;

    public TestController(ExampleService exampleService, CircuitBreakerCustomService circuitBreakerCustomService) {
        this.exampleService = exampleService;
        this.circuitBreakerCustomService = circuitBreakerCustomService;
    }

    @PostMapping("/sync")
    public String sync() {
//        log.info("Starting sync call to app1");
        String response = app1Service.getHelloFromApp1();
//        log.info("Completed sync call to app1 with response: {}", response);
        return "Response from app1: " + response;
    }

    @GetMapping("/call-app1-async")
    public CompletableFuture<String> callApp1Async() {
        loggingService.logInfo("before calling");

        return asyncApp1Service.callApp1Async()
                .thenApply(response -> {
                    loggingService.logInfo("Received async response from app1 then return---");
                    return "Async response from app1: " + response;
                });
    }

    @GetMapping("/call-app1-async-error")
    public CompletableFuture<String> callApp1AsyncError() {
        return asyncApp1Service.callApp1AsyncError()
                .thenApply(response -> {
                    loggingService.logInfo("Received async response from app1 then return---");
                    return "Async response from app1: " + response;
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

//    @PostMapping("/call-app1-resilience")
//    public CompletableFuture<String> callApp1Resilience() {
//        return app1Service.callApp1Resilience()
//                .thenApply(response -> {
//                    loggingService.logInfo("Received async response from app1 then return---");
//                    return "Async response from app1: " + response;
//                });
//    }

    @GetMapping("/test-circuit-breaker")
    public ResponseEntity<ApiResponse<String>> testCircuitBreaker() {
        return exampleService.doSomething();
    }

    @GetMapping("/test-circuit-breaker/success")
    public ResponseEntity<ApiResponse<String>> testCircuitBreakerSuccess() {
        loggingService.logInfo("Calling circuit breaker success endpoint");
        return exampleService.serviceWithSuccessfulResponse();
    }

    @GetMapping("/test-circuit-breaker/failure")
    public ResponseEntity<ApiResponse<String>> testCircuitBreakerFailure() {
       return exampleService.serviceWithFailureResponse();
    }

    @GetMapping("/test-circuit-breaker/timeout")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> testCircuitBreakerTimeout() {
        return exampleService.serviceWithTimeout();
    }

    @GetMapping("/test-circuit-breaker/status")
    public ResponseEntity<ApiResponse<String>> getCircuitBreakerStatus() {
        return exampleService.getCircuitBreakerStatus();
    }

//    @PostMapping("/test-circuit-breaker/custom")
//    public CompletableFuture<String> testCircuitBreakerCustom(@RequestBody CircuitBreakerTestRequest request) {
//        loggingService.logInfo("Testing circuit breaker for bank: " + request.getBankName()
//            + ", operation: " + request.getOperation());
//        return circuitBreakerCustomService.testWithCustomConfig(request);
//    }

//     @GetMapping("/test-circuit-breaker/custom/status/{bankName}")
//     public String getCustomCircuitBreakerStatus(@PathVariable String bankName) {
//         loggingService.logInfo("Checking circuit breaker status for bank: " + bankName);
//         return circuitBreakerCustomService.getCircuitBreakerStatus(bankName);
//     }
}
