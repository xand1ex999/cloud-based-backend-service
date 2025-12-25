package com.example.demo.dto;

import com.example.demo.entity.enums.AccountStatus;
import com.example.demo.entity.enums.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AccountAdminResponse {

    private UUID id;
    private UUID userId;
    private Currency currency;
    private BigDecimal balance;
    private AccountStatus status;
    private Instant createdAt;
}
