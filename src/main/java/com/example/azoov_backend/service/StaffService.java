package com.example.azoov_backend.service;

import com.example.azoov_backend.model.Staff;
import com.example.azoov_backend.model.User;
import com.example.azoov_backend.repository.StaffRepository;
import com.example.azoov_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    public List<Staff> getAllStaff(Long businessId) {
        return staffRepository.findByUserBusinessId(businessId);
    }

    public Staff getStaffById(Long id, Long businessId) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        if (!staff.getUser().getBusiness().getId().equals(businessId)) {
            throw new RuntimeException("Staff not found");
        }
        return staff;
    }

    @Transactional
    public Staff createStaff(Staff staff, Long businessId) {
        if (staff.getUser().getBusiness().getId().equals(businessId)) {
            return staffRepository.save(staff);
        }
        throw new RuntimeException("Invalid business");
    }

    @Transactional
    public Staff updateStaff(Long id, Staff staffData, Long businessId) {
        Staff staff = getStaffById(id, businessId);
        staff.setDepartment(staffData.getDepartment());
        staff.setPosition(staffData.getPosition());
        staff.setSalary(staffData.getSalary());
        staff.setHireDate(staffData.getHireDate());
        return staffRepository.save(staff);
    }

    @Transactional
    public void deleteStaff(Long id, Long businessId) {
        Staff staff = getStaffById(id, businessId);
        staffRepository.delete(staff);
    }

    public List<User> getActiveStaff(Long businessId) {
        return userRepository.findAll().stream()
                .filter(u -> u.getBusiness() != null && u.getBusiness().getId().equals(businessId))
                .filter(User::getActive)
                .collect(Collectors.toList());
    }
}

