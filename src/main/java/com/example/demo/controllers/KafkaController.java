package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.KafkaProducerService;
import com.example.demo.services.LoggingService;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {
    @Autowired
    private LoggingService loggingService;

    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
        loggingService.logInfo(message); //("Received request to send message via Kafka: {}", message);
        kafkaProducerService.sendMessage(message);
        return "Message sent to Kafka!";
    }
}