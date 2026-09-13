package org.example.loanservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Loan {
    private String loanId;
    private Long customerId;
    private Double amount;
    private Integer termMonths;
    private Double interestRate;
    private String purpose;
    private String status;
}
