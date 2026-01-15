package com.example.azoov_backend.repository;

import com.example.azoov_backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByBusinessId(Long businessId);
    Optional<Customer> findByEmailAndBusinessId(String email, Long businessId);
    List<Customer> findByBusinessIdAndNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContaining(
        Long businessId, String name, String email, String phone
    );
}

