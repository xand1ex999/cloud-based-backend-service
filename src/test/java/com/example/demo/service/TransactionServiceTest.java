package com.example.demo.service;

import com.example.demo.dto.CustomUserDetails;
import com.example.demo.dto.TransactionCreateRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.Currency;
import com.example.demo.entity.enums.Role;
import com.example.demo.entity.enums.TransactionType;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    /* ---------------- HELPERS ---------------- */

    private User mockUserEntity(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("test@mail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        return user;
    }

    private CustomUserDetails mockUserDetails(Long id) {
        return new CustomUserDetails(mockUserEntity(id));
    }

    private Account mockAccount(UUID accountId, Long userId, BigDecimal balance) {
        Account account = new Account();
        account.setId(accountId);
        account.setBalance(balance);
        User user = new User();
        user.setId(userId);
        account.setUser(user);
        return account;
    }

    /* -------------------------------------- */

    @Test
    void createTransaction_deposit_success() {
        Long userId = 1L;
        UUID accountId = UUID.randomUUID();

        CustomUserDetails user = mockUserDetails(userId);

        Account account = mockAccount(
                accountId,
                userId,
                BigDecimal.valueOf(100)
        );

        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setAccountId(accountId);
        request.setAmount(BigDecimal.valueOf(50));
        request.setCurrency(Currency.USD);
        request.setTransactionType(TransactionType.DEPOSIT);

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.createTransaction(request, user);

        assertEquals(BigDecimal.valueOf(150), account.getBalance());
        assertEquals(TransactionType.DEPOSIT, response.getTransactionType());
    }

    @Test
    void createTransaction_withdraw_success() {
        Long userId = 1L;
        UUID accountId = UUID.randomUUID();

        CustomUserDetails user = mockUserDetails(userId);

        Account account = mockAccount(
                accountId,
                userId,
                BigDecimal.valueOf(100)
        );

        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setAccountId(accountId);
        request.setAmount(BigDecimal.valueOf(50));
        request.setCurrency(Currency.USD);
        request.setTransactionType(TransactionType.WITHDRAW);

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.createTransaction(request, user);

        assertEquals(BigDecimal.valueOf(50), account.getBalance());
        assertEquals(TransactionType.WITHDRAW, response.getTransactionType());
    }

    @Test
    void createTransaction_accountNotFound() {
        UUID accountId = UUID.randomUUID();

        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setAccountId(accountId);
        request.setAmount(BigDecimal.valueOf(50));
        request.setCurrency(Currency.USD);
        request.setTransactionType(TransactionType.DEPOSIT);

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> transactionService.createTransaction(request, mockUserDetails(1L)));

        assertEquals("Account not found", ex.getMessage());

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_withdraw_insufficientBalance() {
        UUID accountId = UUID.randomUUID();
        Long userId = 1L;

        Account account = mockAccount(accountId, userId, BigDecimal.valueOf(30));

        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setAccountId(accountId);
        request.setAmount(BigDecimal.valueOf(50));
        request.setCurrency(Currency.USD);
        request.setTransactionType(TransactionType.WITHDRAW);

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> transactionService.createTransaction(request, mockUserDetails(userId)));

        assertEquals("Insufficient balance", ex.getMessage());

        assertEquals(BigDecimal.valueOf(30), account.getBalance());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_accountDoesNotBelongToUser() {
        UUID accountId = UUID.randomUUID();

        Account account = mockAccount(accountId, 2L, BigDecimal.valueOf(100));

        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setAccountId(accountId);
        request.setAmount(BigDecimal.valueOf(10));
        request.setCurrency(Currency.USD);
        request.setTransactionType(TransactionType.DEPOSIT);

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> transactionService.createTransaction(request, mockUserDetails(1L)));

        assertEquals("Access denied to this account", ex.getMessage());

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_amountZero_shouldThrowException() {
        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setAmount(BigDecimal.ZERO);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transactionService.createTransaction(
                        request, mockUserDetails(1L)));

        assertEquals("Amount must be positive", ex.getMessage());

        verify(accountRepository, never()).findById(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_insufficientBalance_shouldThrowException() {
        UUID accountId = UUID.randomUUID();

        Account account = mockAccount(accountId, 1L, BigDecimal.valueOf(50));

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setAccountId(accountId);
        request.setTransactionType(TransactionType.WITHDRAW);
        request.setAmount(BigDecimal.valueOf(100));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> transactionService.createTransaction(request, mockUserDetails(1L)));

        assertEquals("Insufficient balance", ex.getMessage());

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_transactionSavedCorrectly() {
        UUID accountId = UUID.randomUUID();

        Account account = mockAccount(accountId, 1L, BigDecimal.valueOf(100));

        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setAccountId(accountId);
        request.setAmount(BigDecimal.valueOf(20));
        request.setCurrency(Currency.USD);
        request.setTransactionType(TransactionType.DEPOSIT);

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.createTransaction(request, mockUserDetails(1L));

        assertEquals(BigDecimal.valueOf(120), account.getBalance());
        assertEquals(TransactionType.DEPOSIT, response.getTransactionType());
    }

}
