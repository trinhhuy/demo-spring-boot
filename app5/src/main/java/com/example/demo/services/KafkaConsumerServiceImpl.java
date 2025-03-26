package com.example.demo.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.repositories.OrderRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumerServiceImpl {
    private final OrderRepository orderRepository;
    private final KafkaProducerServiceImpl kafkaProducerService;
    private final PlatformTransactionManager transactionManager;

    public KafkaConsumerServiceImpl(
            OrderRepository orderRepository,
            KafkaProducerServiceImpl kafkaProducerService,
            PlatformTransactionManager transactionManager) {
        this.kafkaProducerService = kafkaProducerService;
        this.orderRepository = orderRepository;
        this.transactionManager = transactionManager;
    }

    @KafkaListener(
            topics = "${spring.kafka.producer.topic.payment-success}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listenPaymentSuccess(ConsumerRecord<String, String> message) {
        log.info("Received key success: {}, message: {}", message.key(), message.value());
    }

    @KafkaListener(
            topics = "${spring.kafka.producer.topic.payment-failed}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listenPaymentFailed(ConsumerRecord<String, String> message) {
        log.info("Received key failed: {}, message: {}", message.key(), message.value());
    }
}
