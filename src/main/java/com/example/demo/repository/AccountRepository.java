package com.example.demo.repository;

import com.example.demo.dto.AccountResponse;
import com.example.demo.entity.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository  extends CrudRepository<Account, UUID> {
    List<Account> findAllByUserId(Long userId);
}
