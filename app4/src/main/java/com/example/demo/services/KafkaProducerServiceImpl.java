package com.example.demo.services;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KafkaProducerServiceImpl {
    KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String key, String message) {
        kafkaTemplate.send(topic, key, message).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                        "Message sent to topic {} with offset {}",
                        topic,
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message to Kafka", ex);
            }
        });
    }
}
