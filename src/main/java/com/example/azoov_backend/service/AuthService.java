package com.example.azoov_backend.service;

import com.example.azoov_backend.dto.LoginRequest;
import com.example.azoov_backend.dto.RegisterRequest;
import com.example.azoov_backend.repository.BusinessRepository;
import com.example.azoov_backend.model.Business;
import com.example.azoov_backend.model.Role;
import com.example.azoov_backend.model.User;
import com.example.azoov_backend.repository.UserRepository;
import com.example.azoov_backend.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final com.example.azoov_backend.repository.PasswordResetOTPRepository otpRepository;

    public AuthService(UserRepository userRepository, BusinessRepository businessRepository,
            PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            @Lazy AuthenticationManager authenticationManager,
            EmailService emailService,
            com.example.azoov_backend.repository.PasswordResetOTPRepository otpRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.otpRepository = otpRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public Map<String, Object> register(@Valid @org.jetbrains.annotations.UnknownNullability RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Business business = new Business();
        business.setName("My Business");
        business = businessRepository.save(business);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole() != null ? request.getRole() : Role.SALES);
        user.setBusiness(business);
        user.setActive(true);

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        return response;
    }

    public Map<String, Object> login(@Valid @org.jetbrains.annotations.UnknownNullability LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = (User) loadUserByUsername(request.getEmail());
        user.setLastLogin(new java.util.Date());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        return response;
    }

    public Map<String, Object> getMe(String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        return response;
    }

    @Transactional
    public Map<String, Object> forgotPassword(String email) {
        // Verify user exists (throws exception if not found)
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        // Delete any existing OTPs for this email
        otpRepository.deleteByEmail(email);

        // Generate 6-digit OTP
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));

        // Create OTP entity with 10-minute expiry
        com.example.azoov_backend.model.PasswordResetOTP otpEntity = new com.example.azoov_backend.model.PasswordResetOTP();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(java.time.LocalDateTime.now().plusMinutes(10));
        otpEntity.setUsed(false);
        otpRepository.save(otpEntity);

        // Send OTP via email
        emailService.sendOTPEmail(email, otp);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "OTP has been sent to your email");
        response.put("email", email);
        return response;
    }

    @Transactional
    public Map<String, Object> verifyOTPAndResetPassword(String email, String otp, String newPassword) {
        // Find OTP
        com.example.azoov_backend.model.PasswordResetOTP otpEntity = otpRepository
                .findByEmailAndOtpAndUsedFalse(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        // Check if expired
        if (otpEntity.isExpired()) {
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark OTP as used
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        return response;
    }
}
