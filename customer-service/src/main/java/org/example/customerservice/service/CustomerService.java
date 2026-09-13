package org.example.customerservice.service;

import lombok.RequiredArgsConstructor;
import org.example.customerservice.entity.Customer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    private final List<Customer> customers = new ArrayList<>();

    public CustomerService() {
        customers.add(new Customer(1L, "John Doe", "123 Main St", 30, "123-456-7890"));
        customers.add(new Customer(2L, "Jane Doe", "456 Elm St", 25, "987-654-3210"));
        customers.add(new Customer(3L, "John Smith", "789 Oak St", 40, "555-555-5555"));
        customers.add(new Customer(4L, "Jane Smith", "101 Pine St", 35, "555-1212-1212"));
    }

    public Customer findById(Long id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
