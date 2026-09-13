package org.example.loanservice.dto;

public record CreateLoanRequest(
        Long customerId,
        Double amount,
        Integer termMonths,
        Double interestRate,
        String purpose
) {
}
