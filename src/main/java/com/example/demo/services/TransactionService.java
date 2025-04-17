package com.example.demo.services;


import com.example.demo.adapter.grpc.client.AccountGrpcClient;
import com.example.demo.adapter.grpc.client.TransactionGrpcClient;
import com.example.demo.dto.request.transaction.TransactionRequestDto;
import com.example.demo.dto.response.ApiResponseDto;
import com.example.demo.dto.response.transaction.TransactionResponseDto;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import digitaldocuments.library.v1.account.AccountMessages;
import digitaldocuments.library.v1.transaction.TransactionMessages;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.mapper.TransactionMapper.*;

@Service
public class TransactionService {

    private final TransactionGrpcClient transactionGrpcClient;
    private final AccountGrpcClient accountGrpcClient;
    private final UserRepository userRepository;

    public TransactionService(
            UserRepository userRepository,
            AccountGrpcClient accountGrpcClient,
            TransactionGrpcClient transactionGrpcClient) {
        this.userRepository = userRepository;
        this.accountGrpcClient = accountGrpcClient;
        this.transactionGrpcClient = transactionGrpcClient;
    }

    public ResponseEntity<ApiResponseDto<TransactionResponseDto>> transaction(TransactionRequestDto request) {
        // check account isOwner
        validUserIsOwner(request.getAccountId());
        //
        TransactionMessages.CreateTransactionRequest transactionRequest = toTransactionRequestDto(request);
        TransactionMessages.CreateTransactionResponse res = transactionGrpcClient.createTransaction(transactionRequest);

        return ApiResponseDto.success(toTransactionResponseDto(res));
    }

    private void validUserIsOwner(Long accountId) {
        User user = getCurrentUser();
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        AccountMessages.ListAccountsRequest getListAccountRequest = AccountMessages.ListAccountsRequest.newBuilder()
                .setUserId(user.getId())
                .build();
        AccountMessages.ListAccountsResponse accounts = accountGrpcClient.listAccounts(getListAccountRequest);
        List<Long> accountIds = accounts.getAccountsList().stream().map(AccountMessages.GetAccountResponse::getId).toList();

        if (!accountIds.contains(accountId)) {
            throw new IllegalArgumentException("account service: Account not found");
        }
    }


    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }

    private User getCurrentUser() {
        String username = getCurrentUsername();
        if (username != null) {
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }
}
