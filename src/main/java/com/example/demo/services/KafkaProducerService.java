package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.services.LoggingService;

@Service
public class KafkaProducerService {
    @Autowired
    private LoggingService loggingService;

    private static final String TOPIC = "app-communication";
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void sendMessage(String message) {
        loggingService.logInfo("Sending message to Kafka: " + message);
        
        kafkaTemplate.send(TOPIC, message)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    loggingService.logInfo("Message test sent to topic " + TOPIC + " with offset " + result.getRecordMetadata().offset());
                } else {
                    loggingService.logError("Failed to send message to Kafka" + ex.getMessage());
                }
            });
    }
}