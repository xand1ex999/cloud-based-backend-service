package com.example.demo.dto;

import com.example.demo.entity.enums.Currency;
import com.example.demo.entity.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreateRequest {
    @NotNull
    private UUID accountId;
    @NotNull
    private TransactionType transactionType;
    @NotNull
    private Currency currency;
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal amount;
}
