package com.example.demo.controller;

import com.example.demo.dto.AccountCreateRequest;
import com.example.demo.dto.AccountResponse;
import com.example.demo.dto.CustomUserDetails;
import com.example.demo.service.AccountService;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountCreateRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok().body(accountService.createAccount(request, user.getId()));
    }

    /**
     * GET MY ACCOUNTS
     * USER sees only his accounts
     */

    @GetMapping("/my")
    public List<AccountResponse> getMyAccounts(@AuthenticationPrincipal CustomUserDetails user) {
        return accountService.getUserAccounts(user.getId());
    }


    /**
     * GET ACCOUNT BY ID
     * USER -> only if owner
     * ADMIN -> any account
     */

    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponse getAccountById(@PathVariable UUID accountId) {
        return accountService.getAccountById(accountId);
    }
//

    /**
     * GET ALL ACCOUNTS
     * ADMIN ONLY
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AccountResponse> getAllAccounts(Pageable pageable) {
        return accountService.getAllAccounts(pageable);
    }

    /**
     * FREEZE ACCOUNT
     * ADMIN ONLY
     */
    @PatchMapping("/{accountId}/freeze")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void freezeAccount(@PathVariable UUID accountId) {
        accountService.freezeAccount(accountId);
    }

    /**
     * UNFREEZE ACCOUNT
     * ADMIN ONLY
     */
    @PatchMapping("/{accountId}/unfreeze")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void unfreezeAccount(@PathVariable UUID accountId) {
        accountService.unfreezeAccount(accountId);
    }

    @GetMapping("/debug")
    public Object debug(@AuthenticationPrincipal CustomUserDetails user) {
        return Map.of(
                "id", user.getId(),
                "email", user.getUsername(),
                "roles", user.getAuthorities()
        );
    }
}
