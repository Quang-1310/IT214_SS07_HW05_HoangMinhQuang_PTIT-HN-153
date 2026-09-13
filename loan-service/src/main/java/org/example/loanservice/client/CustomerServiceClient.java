package org.example.loanservice.client;

import org.example.loanservice.entity.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("customer-service")
public interface CustomerServiceClient {
    @GetMapping("/api/v1/customers/{id}")
    Customer findById(@PathVariable Long id);
}
