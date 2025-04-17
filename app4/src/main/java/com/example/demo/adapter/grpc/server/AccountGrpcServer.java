package com.example.demo.adapter.grpc.server;

import com.vtbank.digitaldocuments.library.v1.AccountServiceGrpc;
import com.example.demo.services.AccountService;
import digitaldocuments.library.v1.account.AccountMessages.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@GrpcService
public class AccountGrpcServer extends AccountServiceGrpc.AccountServiceImplBase {

    private final AccountService accountService;

    @Autowired
    public AccountGrpcServer(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void getAccount(GetAccountRequest request, StreamObserver<GetAccountResponse> responseObserver) {
        GetAccountResponse account = accountService.getAccountById(request);
        responseObserver.onNext(account);
        responseObserver.onCompleted();
    }

    @Override
    public void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
        CreateAccountResponse account = accountService.createAccount(request);
        responseObserver.onNext(account);
        responseObserver.onCompleted();
    }

    @Override
    public void listAccounts(ListAccountsRequest request, StreamObserver<ListAccountsResponse> responseObserver) {
        ListAccountsResponse accounts = accountService.getAccountsByUserId(request);
        responseObserver.onNext(accounts);
        responseObserver.onCompleted();
    }

} 