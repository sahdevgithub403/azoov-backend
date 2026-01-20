package com.example.azoov_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardStats {
    private Double totalRevenue;
    private Long activeUsers;
    private Long totalOrders;
    private String systemHealth;
}
