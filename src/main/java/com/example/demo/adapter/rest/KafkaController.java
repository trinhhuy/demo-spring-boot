package com.example.demo.adapter.rest;

import com.example.demo.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adapter.kafka.producer.KafkaProducer;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {
    @Autowired
    private LoggingService loggingService;
    
    @Autowired
    private KafkaProducer kafkaProducerService;
    
    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
        loggingService.logInfo("Received request to send message via Kafka: {}");
        kafkaProducerService.sendMessage(message);
        return "Message sent to Kafka!";
    }
}