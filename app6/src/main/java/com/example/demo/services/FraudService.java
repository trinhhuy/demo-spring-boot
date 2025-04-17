package com.example.demo.services;

import com.example.demo.adapter.kafka.producer.KafkaProducer;
import com.example.demo.dto.message.account.AccountBalanceFailedMessageDto;
import com.example.demo.dto.message.account.AccountBalanceUpdatedMessageDto;
import com.example.demo.models.FraudDetection;
import com.example.demo.repositories.FraudDetectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;


import static com.example.demo.mapper.FraudMapper.*;

@Slf4j
@Service
public class FraudService {

    @Value("${spring.kafka.producer.topic.fraud.transaction.verified}")
    private String fraudTransactionVerifiedTopic ;
    @Value("${spring.kafka.producer.topic.fraud.transaction.detected}")
    private String fraudTransactionDetectedTopic ;

    private final FraudDetectionRepository fraudDetectionRepository;
    private final KafkaProducer kafkaProducer;
    private final TransactionTemplate txTemplate;

    public FraudService(
            FraudDetectionRepository fraudDetectionRepository,
            KafkaProducer kafkaProducer,
            PlatformTransactionManager transactionManager) {
        this.fraudDetectionRepository = fraudDetectionRepository;
        this.kafkaProducer = kafkaProducer;
        this.txTemplate = new TransactionTemplate(transactionManager);
    }


    public void fraudDetection(AccountBalanceUpdatedMessageDto transactionMessage) {
        txTemplate.executeWithoutResult(status -> {
            FraudDetection fraudDetection = new FraudDetection();
            fraudDetection.setTransactionId(transactionMessage.getTransactionId());

            if (Math.random() < 0.5) {
                String reason = "Error: fraud detected";
                fraudDetection.setIsFraudulent(true);
                fraudDetection.setReason(reason);
                fraudDetectionRepository.save(fraudDetection);

                sendFraudTransactionDetected(transactionMessage.getTransactionId(), reason);
            } else {
                fraudDetection.setIsFraudulent(false);
                fraudDetectionRepository.save(fraudDetection);

                sendFraudTransactionVerified(transactionMessage.getTransactionId());
            }
        });
    }


    private void sendFraudTransactionVerified(Long transactionId) {
        String messageKey = transactionId.toString();
        AccountBalanceUpdatedMessageDto accountBalanceUpdatedMessageDto = new AccountBalanceUpdatedMessageDto();
        accountBalanceUpdatedMessageDto.setTransactionId(transactionId);

        String messageValue = toMessage(accountBalanceUpdatedMessageDto);
        kafkaProducer.sendMessage(fraudTransactionVerifiedTopic, messageKey, messageValue);
    }

    private void sendFraudTransactionDetected(Long transactionId, String reason) {
        String messageKey = transactionId.toString();
        AccountBalanceFailedMessageDto accountBalanceFailedMessageDto = new AccountBalanceFailedMessageDto ();
        accountBalanceFailedMessageDto.setTransactionId(transactionId);
        accountBalanceFailedMessageDto.setReason(reason);

        String messageValue = toMessage(accountBalanceFailedMessageDto);
        kafkaProducer.sendMessage(fraudTransactionDetectedTopic, messageKey, messageValue);
    }
} 