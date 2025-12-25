package com.example.demo.service;

import com.example.demo.dto.AccountCreateRequest;
import com.example.demo.dto.AccountResponse;
import com.example.demo.dto.CustomUserDetails;
import com.example.demo.entity.Account;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.AccountStatus;
import com.example.demo.mapper.AccountMapper;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponse createAccount(AccountCreateRequest requestDto, Long userId) {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id" + userId + "not found"));
        Account account = new Account();
        account.setUser(user);
        account.setCurrency(requestDto.getCurrency());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(Instant.now());

        Account saved = accountRepository.save(account);

        return AccountMapper.toResponse(saved);
    }

    public List<AccountResponse> getUserAccounts(Long userId) {
        List<Account> accounts = accountRepository.findAllByUserId(userId);
        return accounts.stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

}
