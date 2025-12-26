package com.example.demo.dto;

import com.example.demo.entity.enums.Currency;
import com.example.demo.entity.enums.TransactionStatus;
import com.example.demo.entity.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private UUID transactionId;
    private UUID accountId;
    private BigDecimal amount;
    private Currency currency;
    private TransactionType transactionType;
    private TransactionStatus status;
    private Instant createdAt;
}
