package com.example.demo.adapter.kafka.consumer.transaction;

import com.example.demo.dto.message.account.AccountBalanceUpdatedMessageDto;
import com.example.demo.dto.message.account.AccountBalanceFailedMessageDto;
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
public class AccountConsumer {

    private final TransactionService transactionService;
    public AccountConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(
        topics = "${spring.kafka.producer.topic.account.balance.updated}",
        groupId = "${spring.kafka.consumer.group-id}")
    public void listenAccountBalanceUpdated(ConsumerRecord<String, String> message, Acknowledgment ack) {
        AccountBalanceUpdatedMessageDto transactionCompletedMessageDto = parseMessage(message.value(), new TypeReference<AccountBalanceUpdatedMessageDto>() {});

        transactionService.updateTransactionStatus(transactionCompletedMessageDto.getTransactionId(), TransactionStatus.PENDING_RESERVE);

        ack.acknowledge();
    }

    @KafkaListener(
            topics = "${spring.kafka.producer.topic.account.balance.failed}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listenAccountBalanceFailed(ConsumerRecord<String, String> message, Acknowledgment ack) {
        AccountBalanceFailedMessageDto transactionFailedMessageDto = parseMessage(message.value(), new TypeReference<AccountBalanceFailedMessageDto>() {});

        transactionService.updateTransactionStatus(transactionFailedMessageDto.getTransactionId(), TransactionStatus.FAILED);

        ack.acknowledge();
    }

}
