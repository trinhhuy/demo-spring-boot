// app1/src/main/java/com/example/demo/controller/KafkaResponseController.java
package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private static final Logger log = LoggerFactory.getLogger(KafkaController.class);
    
    @GetMapping("/status")
    public String getStatus() {
        log.info("Kafka consumer status requested");
        return "Kafka consumer is running!";
    }
}