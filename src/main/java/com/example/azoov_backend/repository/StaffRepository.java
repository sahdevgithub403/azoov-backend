package com.example.azoov_backend.repository;

import com.example.azoov_backend.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByUserBusinessId(Long businessId);
    Optional<Staff> findByUserId(Long userId);
}

