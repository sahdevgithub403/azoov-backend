package com.minierp.controller;

import com.minierp.model.Business;
import com.minierp.model.User;
import com.minierp.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {
    private final BusinessService businessService;

    @GetMapping
    public ResponseEntity<Business> getBusiness(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(businessService.getBusinessById(user.getBusiness().getId()));
    }

    @PutMapping
    public ResponseEntity<Business> updateBusiness(@RequestBody Business business, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(businessService.updateBusiness(user.getBusiness().getId(), business));
    }
}

