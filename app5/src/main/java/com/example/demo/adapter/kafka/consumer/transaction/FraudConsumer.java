package com.example.demo.adapter.kafka.consumer.transaction;

import com.example.demo.dto.message.account.AccountBalanceFailedMessageDto;
import com.example.demo.dto.message.account.AccountBalanceUpdatedMessageDto;
import com.example.demo.dto.message.fraud.FraudTransactionDetectedMessageDto;
import com.example.demo.dto.message.fraud.FraudTransactionVerifiedMessageDto;
import com.example.demo.enums.TransactionStatus;
import com.example.demo.services.TransactionService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import static com.example.demo.mapper.TransactionMapper.*;

@Service
@Slf4j
public class FraudConsumer {
    private final TransactionService transactionService;
    public FraudConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(
            topics = "${spring.kafka.producer.topic.fraud.transaction.verified}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listenFraudTransactionVerified(ConsumerRecord<String, String> message, Acknowledgment ack) {
        FraudTransactionVerifiedMessageDto transactionFailedMessageDto = parseMessage(message.value(), new TypeReference<FraudTransactionVerifiedMessageDto>() {});

        transactionService.updateTransactionStatus(transactionFailedMessageDto.getTransactionId(), TransactionStatus.COMPLETED);

        ack.acknowledge();
    }

    @KafkaListener(
            topics = "${spring.kafka.producer.topic.fraud.transaction.detected}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listenFraudTransactionDetected(ConsumerRecord<String, String> message, Acknowledgment ack) {
        FraudTransactionDetectedMessageDto transactionFailedMessageDto = parseMessage(message.value(), new TypeReference<FraudTransactionDetectedMessageDto>() {});

        transactionService.updateTransactionStatus(transactionFailedMessageDto.getTransactionId(), TransactionStatus.FAILED);

        ack.acknowledge();
    }
}
