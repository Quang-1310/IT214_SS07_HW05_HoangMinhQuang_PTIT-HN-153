package org.example.loanservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.loanservice.dto.CreateLoanRequest;
import org.example.loanservice.entity.Loan;
import org.example.loanservice.service.LoanService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    public Loan createLoan(@RequestBody CreateLoanRequest request) {
        return loanService.createLoan(request);
    }
}
