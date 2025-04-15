package com.example.demo.controllers;

import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.MDC;

import com.example.demo.services.App1Service;
import com.example.demo.services.AsyncApp1Service;
import com.example.demo.services.ReactiveApp1Service;
import com.example.demo.services.LoggingService;
import com.example.demo.services.CacheService;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private App1Service app1Service;

    @Autowired
    private ReactiveApp1Service reactiveApp1Service;

    @Autowired
    private AsyncApp1Service asyncApp1Service;

    @Autowired
    private CacheService cacheService;

    // Cache Test Endpoints
    @GetMapping("/cache/{id}")
    public String getCachedData(@PathVariable String id) {
        log.info("Getting data for id: {}", id);
        return cacheService.getData(id);
    }

    @PostMapping("/cache/{id}")
    public String updateCachedData(@PathVariable String id, @RequestBody String data) {
        log.info("Updating data for id: {}", id);
        return cacheService.updateData(id, data);
    }

    @DeleteMapping("/cache/{id}")
    public void deleteCachedData(@PathVariable String id) {
        log.info("Deleting data for id: {}", id);
        cacheService.deleteData(id);
    }

    @DeleteMapping("/cache/clear")
    public void clearAllCache() {
        log.info("Clearing all cache");
        cacheService.clearAllCache();
    }

    // Performance Test Endpoints
    @GetMapping("/performance/single/{id}")
    public PerformanceResult testSingleRequest(@PathVariable String id) {
        long startTime = System.currentTimeMillis();
        String result = cacheService.getData(id);
        long endTime = System.currentTimeMillis();
        
        return new PerformanceResult(
            "Single request with cache",
            endTime - startTime,
            result
        );
    }

    @GetMapping("/performance/single-no-cache/{id}")
    public PerformanceResult testSingleRequestNoCache(@PathVariable String id) {
        long startTime = System.currentTimeMillis();
        String result = cacheService.getDataWithoutCache(id);
        long endTime = System.currentTimeMillis();
        
        return new PerformanceResult(
            "Single request without cache",
            endTime - startTime,
            result
        );
    }

    @GetMapping("/performance/concurrent/{id}/{requests}")
    public List<PerformanceResult> testConcurrentRequests(
            @PathVariable String id,
            @PathVariable int requests) throws ExecutionException, InterruptedException {
        
        List<CompletableFuture<PerformanceResult>> futures = new ArrayList<>();
        
        // First request (cache miss)
        futures.add(CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            String result = cacheService.getData(id);
            long endTime = System.currentTimeMillis();
            return new PerformanceResult(
                "First request (cache miss)",
                endTime - startTime,
                result
            );
        }));

        // Subsequent requests (cache hits)
        for (int i = 1; i < requests; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                long startTime = System.currentTimeMillis();
                String result = cacheService.getData(id);
                long endTime = System.currentTimeMillis();
                return new PerformanceResult(
                    "Subsequent request (cache hit)",
                    endTime - startTime,
                    result
                );
            }));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        
        List<PerformanceResult> results = new ArrayList<>();
        for (CompletableFuture<PerformanceResult> future : futures) {
            results.add(future.get());
        }
        
        return results;
    }

    @GetMapping("/performance/concurrent-no-cache/{id}/{requests}")
    public List<PerformanceResult> testConcurrentRequestsNoCache(
            @PathVariable String id,
            @PathVariable int requests) throws ExecutionException, InterruptedException {
        
        List<CompletableFuture<PerformanceResult>> futures = new ArrayList<>();
        
        for (int i = 0; i < requests; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                long startTime = System.currentTimeMillis();
                String result = cacheService.getDataWithoutCache(id);
                long endTime = System.currentTimeMillis();
                return new PerformanceResult(
                    "Request without cache",
                    endTime - startTime,
                    result
                );
            }));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        
        List<PerformanceResult> results = new ArrayList<>();
        for (CompletableFuture<PerformanceResult> future : futures) {
            results.add(future.get());
        }
        
        return results;
    }

    @PostMapping("/sync")
    public String sync() {
        String response = app1Service.getHelloFromApp1();
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
        reactiveApp1Service.fireAndForgetReactive();
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

    private static class PerformanceResult {
        private final String testType;
        private final long responseTime;
        private final String result;

        public PerformanceResult(String testType, long responseTime, String result) {
            this.testType = testType;
            this.responseTime = responseTime;
            this.result = result;
        }

        public String getTestType() {
            return testType;
        }

        public long getResponseTime() {
            return responseTime;
        }

        public String getResult() {
            return result;
        }
    }
}
