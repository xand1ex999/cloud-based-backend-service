package com.example.demo.dto;

import com.example.demo.entity.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class AccountCreateRequest {
    @NotNull
    private Currency currency;
}
