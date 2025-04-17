package com.example.demo.adapter.kafka.consumer;

import com.example.demo.dto.message.account.AccountBalanceUpdatedMessageDto;
import com.example.demo.services.FraudService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import static com.example.demo.mapper.FraudMapper.parseMessage;

@Service
@Slf4j
public class AccountConsumer {
    private final FraudService fractureService;

    public AccountConsumer(FraudService fractureService) {
        this.fractureService = fractureService;
    }

    @KafkaListener(
            topics = "${spring.kafka.producer.topic.account.balance.updated}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listenAccountBalanceUpdated(ConsumerRecord<String, String> message, Acknowledgment ack) {
        AccountBalanceUpdatedMessageDto transactionMessage = parseMessage(message.value(), new TypeReference<AccountBalanceUpdatedMessageDto>() {});

        fractureService.fraudDetection(transactionMessage);

        log.info("\n\nsuccessfully consumed message {}", message);
        ack.acknowledge();
    }

}
