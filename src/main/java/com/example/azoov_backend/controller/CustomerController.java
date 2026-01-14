package com.example.azoov_backend.controller;

import com.minierp.model.Customer;
import com.minierp.model.User;
import com.minierp.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(customerService.getAllCustomers(user.getBusiness().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(customerService.getCustomerById(id, user.getBusiness().getId()));
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(customerService.createCustomer(customer, user.getBusiness().getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customer, user.getBusiness().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id, @AuthenticationPrincipal User user) {
        customerService.deleteCustomer(id, user.getBusiness().getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String q, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(customerService.searchCustomers(user.getBusiness().getId(), q));
    }
}

