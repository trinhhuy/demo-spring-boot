package com.example.demo.services;

import com.example.demo.adapter.kafka.producer.KafkaProducer;
import com.example.demo.dto.message.TransactionInitiatedMessageDto;
import com.example.demo.dto.message.account.AccountBalanceFailedMessageDto;
import com.example.demo.dto.message.account.AccountBalanceUpdatedMessageDto;
import com.example.demo.dto.message.fraud.FraudTransactionDetectedMessageDto;
import com.example.demo.dto.message.fraud.FraudTransactionVerifiedMessageDto;
import com.example.demo.enums.AccountReservationStatus;
import com.example.demo.models.Account;
import com.example.demo.models.AccountReservation;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.AccountReservationRepository;
import digitaldocuments.library.v1.account.AccountMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static com.example.demo.mapper.AccountMapper.*;

@Slf4j
@Service
public class AccountService {

    @Value("${spring.kafka.producer.topic.account.balance.updated}")
    private String accountBalanceUpdatedTopic ;
    @Value("${spring.kafka.producer.topic.account.balance.failed}")
    private String accountBalanceFailedTopic ;

    private final AccountRepository accountRepository;
    private final AccountReservationRepository accountReservationRepository;
    private final KafkaProducer kafkaProducer;
    private final TransactionTemplate txTemplate;

    public AccountService(
            AccountRepository accountRepository,
            AccountReservationRepository accountReservationRepository,
            KafkaProducer kafkaProducer,
            PlatformTransactionManager transactionManager) {
        this.accountRepository = accountRepository;
        this.accountReservationRepository = accountReservationRepository;
        this.kafkaProducer = kafkaProducer;
        this.txTemplate = new TransactionTemplate(transactionManager);
    }

   
    @Transactional
    public AccountMessages.CreateAccountResponse createAccount(AccountMessages.CreateAccountRequest request) {
        Double initialBalance = 0.0;
        
        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setBalance(initialBalance);
        accountRepository.save(account);

        return toCreateAccountResponse(account);
    }

   
    public AccountMessages.GetAccountResponse getAccountById(AccountMessages.GetAccountRequest request) {
        Account account = accountRepository.findById(request.getId()).orElse(null);
        if (account == null) {
            throw new EntityNotFoundException("Account with id " + request.getId() + " not found");
        }

        return toAccountResponse(account);
    }

   
    public AccountMessages.ListAccountsResponse getAccountsByUserId(AccountMessages.ListAccountsRequest request) {
        List<Account> accounts = accountRepository.findByUserId(request.getUserId());

        if (accounts == null || accounts.isEmpty()) {
            throw new EntityNotFoundException("Accounts with user_id " + request.getUserId() + " not found");
        }

        return toListAccountResponse(accounts);
    }

    public void completeTransaction(FraudTransactionVerifiedMessageDto transactionMessage) {
        AccountReservation accountReservation = accountReservationRepository.findByTransactionId(transactionMessage.getTransactionId()).orElse(null);
        if (accountReservation == null) {
            log.error("Account reservation with id {} not found. Transaction aborted.", transactionMessage.getTransactionId());
            return;
        }

        switch (accountReservation.getType()) {
            case DEPOSIT -> completeDeposit(accountReservation);
            case WITHDRAW -> completeWithdraw(accountReservation);
            case TRANSFER -> completeTransfer(accountReservation);
        }
    }

    private void completeDeposit(AccountReservation accountReservation) {
        txTemplate.executeWithoutResult(status -> {
            Account account = accountRepository.findById(accountReservation.getAccountId()).orElse(null);
            if (account == null) {
                log.error("Account with id {} not found. Transaction aborted.", accountReservation.getAccountId());
                return;
            }

            account.setBalance(account.getBalance() + accountReservation.getAmount());
            accountRepository.save(account);

            accountReservation.setStatus(AccountReservationStatus.SUCCESS);
            accountReservationRepository.save(accountReservation);
        });
    }

    private void completeWithdraw(AccountReservation accountReservation) {
        txTemplate.executeWithoutResult(status -> {
            accountReservation.setStatus(AccountReservationStatus.SUCCESS);
            accountReservationRepository.save(accountReservation);
        });
    }

    private void completeTransfer(AccountReservation accountReservation) {
        completeWithdraw(accountReservation);
    }

    public void rollbackTransaction(FraudTransactionDetectedMessageDto transactionMessage) {
        AccountReservation accountReservation = accountReservationRepository.findByTransactionId(transactionMessage.getTransactionId()).orElse(null);
        if (accountReservation == null) {
            log.error("Account reservation with id {} not found. Transaction aborted.", transactionMessage.getTransactionId());
            return;
        }

        switch (accountReservation.getType()) {
            case DEPOSIT -> rollbackDeposit(accountReservation);
            case WITHDRAW -> rollbackWithdraw(accountReservation);
            case TRANSFER -> rollbackTransfer(accountReservation);
        }
    }

    private void rollbackDeposit(AccountReservation accountReservation) {
        txTemplate.executeWithoutResult(status -> {
            accountReservation.setStatus(AccountReservationStatus.FAILURE);
            accountReservationRepository.save(accountReservation);
        });
    }

    private void rollbackWithdraw(AccountReservation accountReservation) {
        txTemplate.executeWithoutResult(status -> {
            Account account = accountRepository.findById(accountReservation.getAccountId()).orElse(null);
            if (account == null) {
                log.error("Account with id {} not found. Transaction aborted.", accountReservation.getAccountId());
                return;
            }

            account.setBalance(account.getBalance() + accountReservation.getAmount());
            accountRepository.save(account);

            accountReservation.setStatus(AccountReservationStatus.FAILURE);
            accountReservationRepository.save(accountReservation);
        });
    }

    private void rollbackTransfer(AccountReservation accountReservation) {
        rollbackWithdraw(accountReservation);
    }
   
    public void transaction(TransactionInitiatedMessageDto transactionMessage) {
        switch (transactionMessage.getType()) {
            case DEPOSIT -> deposit(transactionMessage);
            case WITHDRAW -> withdraw(transactionMessage);
            case TRANSFER -> transfer(transactionMessage);
        }
    }

    private void deposit(TransactionInitiatedMessageDto transactionMessage) {
        txTemplate.executeWithoutResult(status -> {
            Account account = accountRepository.findById(transactionMessage.getAccountId()).orElse(null);
            if (account == null) {
                sendAccountBalanceFailedMessage(transactionMessage.getTransactionId(), "Account not found. Transaction aborted.");
                return;
            }

            accountReservationRepository.save(toAccountReservation(transactionMessage));

            sendAccountBalanceUpdatedMessage(transactionMessage.getTransactionId());
        });
    }

    private void withdraw(TransactionInitiatedMessageDto transactionMessage) {
        txTemplate.executeWithoutResult(status -> {
            Account account = accountRepository.findById(transactionMessage.getAccountId()).orElse(null);
            if (account == null) {
                sendAccountBalanceFailedMessage(transactionMessage.getTransactionId(), "Account not found. Transaction aborted.");
                return;
            }

            if (account.getBalance() < transactionMessage.getAmount()) {
                sendAccountBalanceFailedMessage(transactionMessage.getTransactionId(), "Insufficient funds. Transaction aborted.");
                return;
            }


            account.setBalance(account.getBalance() - transactionMessage.getAmount());
            accountRepository.save(account);

            accountReservationRepository.save(toAccountReservation(transactionMessage));

            sendAccountBalanceUpdatedMessage(transactionMessage.getTransactionId());
        });
    }

    private void transfer(TransactionInitiatedMessageDto transactionMessage) {
        withdraw(transactionMessage);
    }


    private void sendAccountBalanceUpdatedMessage(Long transactionId) {
        String messageKey = transactionId.toString();
        AccountBalanceUpdatedMessageDto accountBalanceUpdatedMessageDto = new AccountBalanceUpdatedMessageDto();
        accountBalanceUpdatedMessageDto.setTransactionId(transactionId);

        String messageValue = toMessage(accountBalanceUpdatedMessageDto);
        kafkaProducer.sendMessage(accountBalanceUpdatedTopic, messageKey, messageValue);
    }

    private void sendAccountBalanceFailedMessage(Long transactionId, String reason) {
        String messageKey = transactionId.toString();
        AccountBalanceFailedMessageDto accountBalanceFailedMessageDto = new AccountBalanceFailedMessageDto ();
        accountBalanceFailedMessageDto.setTransactionId(transactionId);
        accountBalanceFailedMessageDto.setReason(reason);

        String messageValue = toMessage(accountBalanceFailedMessageDto);
        kafkaProducer.sendMessage(accountBalanceFailedTopic, messageKey, messageValue);
    }
} 