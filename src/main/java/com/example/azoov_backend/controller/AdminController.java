package com.example.azoov_backend.controller;

import com.example.azoov_backend.dto.AdminDashboardStats;
import com.example.azoov_backend.model.User;
import com.example.azoov_backend.service.AdminService;
import com.example.azoov_backend.service.AuthService;
import com.example.azoov_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private User getAuthenticatedUser(String token) {
        String email = jwtUtil.extractUsername(token.substring(7));
        return (User) userDetailsService.loadUserByUsername(email);
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStats> getDashboardStats(@RequestHeader("Authorization") String token) {
        User admin = getAuthenticatedUser(token);
        return ResponseEntity.ok(adminService.getDashboardStats(admin));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(@RequestHeader("Authorization") String token) {
        User admin = getAuthenticatedUser(token);
        return ResponseEntity.ok(adminService.getUsersByBusiness(admin));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> toggleUserRole(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        User admin = getAuthenticatedUser(token);
        return ResponseEntity.ok(adminService.toggleUserRole(id, admin));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        User admin = getAuthenticatedUser(token);
        adminService.deleteUser(id, admin);
        return ResponseEntity.ok().build();
    }
}
