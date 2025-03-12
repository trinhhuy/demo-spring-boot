// src/main/java/com/example/demo/controller/KafkaController.java
package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import com.example.demo.service.KafkaProducerService;

@Slf4j
@RestController
@RequestMapping("/api/kafka")
public class KafkaController {
    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
        log.info("Received request to send message: {}", message);
        kafkaProducerService.sendMessage(message);
        return "Message sent to Kafka!";
    }
}