package com.minierp.service;

import com.minierp.model.Business;
import com.minierp.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessService {
    private final BusinessRepository businessRepository;

    public Business getBusinessById(Long id) {
        return businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));
    }

    @Transactional
    public Business updateBusiness(Long id, Business businessData) {
        Business business = getBusinessById(id);
        business.setName(businessData.getName());
        business.setDescription(businessData.getDescription());
        business.setAddress(businessData.getAddress());
        business.setEmail(businessData.getEmail());
        business.setPhone(businessData.getPhone());
        business.setLogo(businessData.getLogo());
        business.setTaxId(businessData.getTaxId());
        business.setWebsite(businessData.getWebsite());
        return businessRepository.save(business);
    }
}

