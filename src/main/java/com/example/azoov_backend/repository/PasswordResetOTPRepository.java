package com.example.azoov_backend.repository;

import com.example.azoov_backend.model.PasswordResetOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOTPRepository extends JpaRepository<PasswordResetOTP, Long> {

    Optional<PasswordResetOTP> findByEmailAndOtpAndUsedFalse(String email, String otp);

    void deleteByEmail(String email);
}
