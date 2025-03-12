package com.example.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    @KafkaListener(topics = "app-communication", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.info("Received message from Kafka: {}", message);
        // Process the message here
        processMessage(message);
    }
    
    private void processMessage(String message) {
        log.info("Processing message: {}", message);
        // Add your business logic here
    }
}