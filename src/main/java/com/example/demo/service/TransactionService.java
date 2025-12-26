package com.example.demo.service;

import com.example.demo.dto.CustomUserDetails;
import com.example.demo.dto.TransactionCreateRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.enums.TransactionStatus;
import com.example.demo.entity.enums.TransactionType;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionResponse createTransaction(TransactionCreateRequest request, CustomUserDetails user) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied to this account");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }
        // Balance Logic
        if (request.getTransactionType() == TransactionType.DEPOSIT) {
            account.setBalance(account.getBalance().add(request.getAmount()));
        }

        if (request.getTransactionType() == TransactionType.WITHDRAW) {
            if (account.getBalance().compareTo(request.getAmount()) < 0) {
                throw new RuntimeException("Insufficient funds");
            }
            account.setBalance(account.getBalance().subtract(request.getAmount()));
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setType(request.getTransactionType());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(Instant.now());

        Transaction saved = transactionRepository.save(transaction);

        return TransactionMapper.toResponse(saved);
    }

    public List<TransactionResponse> getAllMyTransactions(CustomUserDetails user) {
        List<Transaction> transactions = transactionRepository.findMyTransactions(user.getId());
        return transactions.stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    public TransactionResponse getTransactionById(UUID id, CustomUserDetails user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return TransactionMapper.toResponse(transaction);
    }

    public Page<TransactionResponse> getAllTransactions(Pageable pageable) {
        return transactionRepository
                .findAll(pageable)
                .map(TransactionMapper::toResponse);
    }

}
