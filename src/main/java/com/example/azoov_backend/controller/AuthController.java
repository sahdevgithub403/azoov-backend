package com.example.azoov_backend.controller;

import com.example.azoov_backend.dto.ForgotPasswordRequest;
import com.example.azoov_backend.dto.LoginRequest;
import com.example.azoov_backend.dto.RegisterRequest;
import com.example.azoov_backend.dto.VerifyOTPRequest;
import com.example.azoov_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.getMe(token.substring(7)));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request.getEmail()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOTP(@Valid @RequestBody VerifyOTPRequest request) {
        return ResponseEntity.ok(authService.verifyOTPAndResetPassword(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword()));
    }
}
