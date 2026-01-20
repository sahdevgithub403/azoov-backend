package com.example.azoov_backend.service;

import com.example.azoov_backend.dto.AdminDashboardStats;
import com.example.azoov_backend.model.Role;
import com.example.azoov_backend.model.User;
import com.example.azoov_backend.repository.InvoiceRepository;
import com.example.azoov_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;

    public AdminDashboardStats getDashboardStats(User admin) {
        Long businessId = admin.getBusiness().getId();

        Double totalRevenue = invoiceRepository.getTotalRevenueByBusinessId(businessId);
        if (totalRevenue == null)
            totalRevenue = 0.0;

        long activeUsers = userRepository.countByBusinessIdAndActiveTrue(businessId);
        long totalOrders = invoiceRepository.countByBusinessId(businessId);

        return AdminDashboardStats.builder()
                .totalRevenue(totalRevenue)
                .activeUsers(activeUsers)
                .totalOrders(totalOrders)
                .systemHealth("98.9%") // Mock value, or implement real health check
                .build();
    }

    public List<User> getUsersByBusiness(User admin) {
        return userRepository.findByBusinessId(admin.getBusiness().getId());
    }

    @Transactional
    public User toggleUserRole(Long userId, User admin) {
        // Ensure user belongs to same business
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!targetUser.getBusiness().getId().equals(admin.getBusiness().getId())) {
            throw new RuntimeException("Unauthorized access"); // Basic security check
        }

        // Cycle through roles: ADMIN → MANAGER → SALES → ADMIN
        switch (targetUser.getRole()) {
            case ADMIN:
                targetUser.setRole(Role.MANAGER);
                break;
            case MANAGER:
                targetUser.setRole(Role.SALES);
                break;
            case SALES:
                targetUser.setRole(Role.ADMIN);
                break;
        }

        return userRepository.save(targetUser);
    }

    @Transactional
    public void deleteUser(Long userId, User admin) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!targetUser.getBusiness().getId().equals(admin.getBusiness().getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        // Don't delete yourself
        if (targetUser.getId().equals(admin.getId())) {
            throw new RuntimeException("Cannot delete yourself");
        }

        userRepository.delete(targetUser);
    }
}
