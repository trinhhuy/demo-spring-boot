package com.example.demo.adapter.rest;

import com.example.demo.dto.request.transaction.TransactionRequestDto;
import com.example.demo.dto.response.ApiResponseDto;
import com.example.demo.dto.response.transaction.TransactionResponseDto;
import com.example.demo.services.TransactionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/digital-documents/library/transactions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TransactionController {

    TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<TransactionResponseDto>> transaction(@Valid @RequestBody TransactionRequestDto requestDto) {
        return transactionService.transaction(requestDto);
    }

}
