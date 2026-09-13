package org.example.loanservice.dto;

import org.example.loanservice.entity.Account;
import org.example.loanservice.entity.Customer;
import org.example.loanservice.entity.Loan;

public record LoanResponse(
        Loan loan,
        Account account,
        Customer customer,
        Double monthlyPayment
) {
}
