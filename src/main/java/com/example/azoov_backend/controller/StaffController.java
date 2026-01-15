package com.example.azoov_backend.controller;

import com.example.azoov_backend.model.Staff;
import com.example.azoov_backend.model.User;
import com.example.azoov_backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(staffService.getAllStaff(user.getBusiness().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaff(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(staffService.getStaffById(id, user.getBusiness().getId()));
    }

    @PostMapping
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(staffService.createStaff(staff, user.getBusiness().getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(@PathVariable Long id, @RequestBody Staff staff, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(staffService.updateStaff(id, staff, user.getBusiness().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id, @AuthenticationPrincipal User user) {
        staffService.deleteStaff(id, user.getBusiness().getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveStaff(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(staffService.getActiveStaff(user.getBusiness().getId()));
    }
}

