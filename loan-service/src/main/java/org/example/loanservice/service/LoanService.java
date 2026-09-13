package org.example.loanservice.service;

import lombok.RequiredArgsConstructor;
import org.example.loanservice.client.AccountServiceClient;
import org.example.loanservice.client.CustomerServiceClient;
import org.example.loanservice.dto.CreateLoanRequest;
import org.example.loanservice.dto.LoanResponse;
import org.example.loanservice.entity.Account;
import org.example.loanservice.entity.Customer;
import org.example.loanservice.entity.Loan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final CustomerServiceClient customerServiceClient;
    private final AccountServiceClient accountServiceClient;
    private final List<Loan> loans = new ArrayList<>();


    public LoanResponse createLoan(CreateLoanRequest request) {
        // Lấy thông tin customer
        Customer customer = customerServiceClient.findById(request.customerId());
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }
        // Kiểm tra customer đã tạo tài khoản chưa
        Account account = accountServiceClient.findByCustomerId(request.customerId());
        if (account == null) {
            throw new RuntimeException("Customer has not created account");
        }
        // Tạo loan
        Loan loan = Loan.builder()
                .loanId(UUID.randomUUID().toString())
                .customerId(request.customerId())
                .amount(request.amount())
                .termMonths(request.termMonths())
                .interestRate(request.interestRate())
                .purpose(request.purpose())
                .status("PENDING")
                .build();

        loans.add(loan);

        // Tính số tiền phải trả mỗi tháng
        Double interest = request.amount() * (request.interestRate() / 100) * (request.termMonths() / 12);
        Double totalPayment = request.amount() + interest;
        Double monthlyPayment = totalPayment / request.termMonths();

        return new LoanResponse(
                loan,
                account,
                customer,
                monthlyPayment
        );
    }
}
