package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.KafkaProducerService;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {
    private static final Logger log = LoggerFactory.getLogger(KafkaController.class);
    
    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
        log.info("Received request to send message via Kafka: {}", message);
        kafkaProducerService.sendMessage(message);
        return "Message sent to Kafka!";
    }
}