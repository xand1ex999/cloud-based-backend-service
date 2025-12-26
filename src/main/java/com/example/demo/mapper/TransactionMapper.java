package com.example.demo.mapper;

import com.example.demo.dto.TransactionResponse;
import com.example.demo.entity.Transaction;

public class TransactionMapper {

    private TransactionMapper() {
        // utility class
    }

    public static TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getId());
        response.setAccountId(transaction.getAccount().getId());
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setTransactionType(transaction.getType());
        response.setStatus(transaction.getStatus());
        response.setCreatedAt(transaction.getCreatedAt());

        return response;
    }

}
