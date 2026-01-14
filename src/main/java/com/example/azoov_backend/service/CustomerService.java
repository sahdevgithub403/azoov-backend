package com.minierp.service;

import com.minierp.model.Business;
import com.minierp.model.Customer;
import com.minierp.repository.BusinessRepository;
import com.minierp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final BusinessRepository businessRepository;

    public List<Customer> getAllCustomers(Long businessId) {
        return customerRepository.findByBusinessId(businessId);
    }

    public Customer getCustomerById(Long id, Long businessId) {
        return customerRepository.findById(id)
                .filter(c -> c.getBusiness().getId().equals(businessId))
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Transactional
    public Customer createCustomer(Customer customer, Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        customer.setBusiness(business);
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer customerData, Long businessId) {
        Customer customer = getCustomerById(id, businessId);
        customer.setName(customerData.getName());
        customer.setEmail(customerData.getEmail());
        customer.setPhone(customerData.getPhone());
        customer.setAddress(customerData.getAddress());
        customer.setCity(customerData.getCity());
        customer.setState(customerData.getState());
        customer.setZipCode(customerData.getZipCode());
        customer.setCountry(customerData.getCountry());
        customer.setStatus(customerData.getStatus());
        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long id, Long businessId) {
        Customer customer = getCustomerById(id, businessId);
        customerRepository.delete(customer);
    }

    public List<Customer> searchCustomers(Long businessId, String query) {
        return customerRepository.findByBusinessIdAndNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContaining(
                businessId, query, query, query
        );
    }
}

