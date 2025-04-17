package com.example.demo.adapter.grpc.client;

import com.vtbank.digitaldocuments.library.v1.AccountServiceGrpc;
import digitaldocuments.library.v1.account.AccountMessages;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class AccountGrpcClient {

    @GrpcClient("account-service")
    private AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub;

    public AccountMessages.ListAccountsResponse listAccounts(AccountMessages.ListAccountsRequest request) {
        return accountServiceBlockingStub.listAccounts(request);
    }
}
