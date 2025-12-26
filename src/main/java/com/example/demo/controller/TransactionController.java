package com.example.demo.controller;

import com.example.demo.dto.CustomUserDetails;
import com.example.demo.dto.TransactionCreateRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    @PostMapping()
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionCreateRequest transactionRequest, @AuthenticationPrincipal CustomUserDetails user) {
        TransactionResponse response = transactionService.createTransaction(transactionRequest, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<TransactionResponse>> getAllMyTransactions(@AuthenticationPrincipal CustomUserDetails user) {
        List<TransactionResponse> response = transactionService.getAllMyTransactions(user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactionById(id, user));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(@AuthenticationPrincipal CustomUserDetails user, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getAllTransactions(pageable));
    }

}
