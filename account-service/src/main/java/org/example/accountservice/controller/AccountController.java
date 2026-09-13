package org.example.accountservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.accountservice.entity.Account;
import org.example.accountservice.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/customer/{id}")
    public Account findByCustomerId(@PathVariable Long id) {
        return accountService.findByCustomerId(id);
    }
}
