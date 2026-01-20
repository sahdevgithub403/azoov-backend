package com.example.azoov_backend.repository;

import com.example.azoov_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    java.util.List<User> findByBusinessId(Long businessId);

    long countByBusinessIdAndActiveTrue(Long businessId);
}
