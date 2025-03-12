package com.example.demo.controllers;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.services.App1Service;
import com.example.demo.services.AsyncApp1Service;
import com.example.demo.services.ReactiveApp1Service;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    private App1Service app1Service;

    @Autowired
    private ReactiveApp1Service reactiveApp1Service;

    @Autowired
    private AsyncApp1Service asyncApp1Service;

    @PostMapping("/sync")
    public String sync() {
        log.info("abc calling");
        return "Response from app1: " + app1Service.getHelloFromApp1();
    }

    @GetMapping("/call-app1-async")
    public CompletableFuture<String> callApp1Async() {
        return asyncApp1Service.callApp1Async()
                .thenApply(response -> "Async response from app1: " + response);
    }

    @GetMapping("/fire-forget")
    public String fireAndForget() {
        // Start the async task but don't wait for it
        // asyncApp1Service.fireAndForget();
        reactiveApp1Service.fireAndForgetReactive();
        // Return immediately
        return "Request initiated, not waiting for result";
    }
}
