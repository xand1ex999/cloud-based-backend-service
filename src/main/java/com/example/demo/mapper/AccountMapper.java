package com.example.demo.mapper;


import com.example.demo.dto.AccountResponse;
import com.example.demo.entity.Account;

public class AccountMapper {

    private AccountMapper() {
    }

    public static AccountResponse toResponse(Account account) {
        AccountResponse dto = new AccountResponse();
        dto.setId(account.getId());
        dto.setCurrency(account.getCurrency());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());
        dto.setCreatedAt(account.getCreatedAt());
        return dto;
    }
}
