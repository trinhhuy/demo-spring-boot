package com.example.demo.services;

import com.example.demo.adapter.kafka.producer.KafkaProducer;
import com.example.demo.enums.TransactionStatus;
import com.example.demo.enums.TransactionType;
import com.example.demo.models.Transaction;
import com.example.demo.repositories.TransactionRepository;
import digitaldocuments.library.v1.transaction.TransactionMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.mapper.TransactionMapper.*;

@Service
public class TransactionService {

    @Value("${spring.kafka.producer.topic.transaction.initiated}")
    private String transactionInitiatedTopic;

    private final KafkaProducer kafkaProducer;
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository, KafkaProducer kafkaProducer) {
        this.transactionRepository = transactionRepository;
        this.kafkaProducer = kafkaProducer;
    }

    
    @Transactional
    public TransactionMessages.CreateTransactionResponse createTransaction(TransactionMessages.CreateTransactionRequest request) {
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        Transaction transaction = new Transaction();
        transaction.setAccountId(request.getAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setType(toDomainType(request.getType()));
        transaction.setStatus(TransactionStatus.INITIATED);
        transactionRepository.save(transaction);

        sendTransactionMessage(transaction, transactionInitiatedTopic);
        return toTransactionResponse(transaction);
    }

    
    @Transactional
    public void updateTransactionStatus(Long transactionId, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
        if (transaction == null) {
            // send .dlt: transaction not found
            throw new IllegalArgumentException("Transaction not found");
        }

        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }

    private void sendTransactionMessage(Transaction transaction, String topic) {
        String messageKey = transaction.getId().toString();
        String messageValue = toMessage(transaction);
        kafkaProducer.sendMessage(topic, messageKey, messageValue);
    }
} 