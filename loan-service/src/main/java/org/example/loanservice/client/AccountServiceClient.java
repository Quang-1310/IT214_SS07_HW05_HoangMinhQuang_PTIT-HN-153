package org.example.loanservice.client;

import org.example.loanservice.entity.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("account-service")
public interface AccountServiceClient {
    @GetMapping("/api/v1/accounts/customer/{id}")
    Account findByCustomerId(@PathVariable Long id);
}
