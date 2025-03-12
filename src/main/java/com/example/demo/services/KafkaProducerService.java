// src/main/java/com/example/demo/service/KafkaProducerService.java
package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "app-communication";
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void sendMessage(String message) {
        log.info("Sending message to Kafka: {}", message);
        kafkaTemplate.send(TOPIC, message)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Message sent successfully to topic {}", TOPIC);
                } else {
                    log.error("Failed to send message to Kafka", ex);
                }
            });
    }
}