package org.example.accountservice.service;

import org.example.accountservice.entity.Account;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    private final List<Account> accounts = new ArrayList<>();

    public AccountService() {
        accounts.add(new Account(1L, 1L, "john.doe@example.com", "password123"));
        accounts.add(new Account(2L, 2L, "jane.doe@example.com", "password456"));
        accounts.add(new Account(3L, 3L, "john.smith@example.com", "password789"));
    }

    public Account findByCustomerId(Long id) {
        return accounts.stream()
                .filter(a -> a.getCustomerId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}
