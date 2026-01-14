package com.example.azoov_backend.service;

import com.example.azoov_backend.dto.InvoiceItemRequest;
import com.minierp.dto.InvoiceRequest;
import com.example.azoov_backend.model.*;
import com.example.azoov_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public List<Invoice> getAllInvoices(Long businessId) {
        return invoiceRepository.findByBusinessId(businessId);
    }

    public Invoice getInvoiceById(Long id, Long businessId) {
        return invoiceRepository.findById(id)
                .filter(i -> i.getBusiness().getId().equals(businessId))
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Transactional
    public Invoice createInvoice(InvoiceRequest request, Long businessId, String userEmail) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        Customer customer = customerRepository.findById(request.getCustomerId())
                .filter(c -> c.getBusiness().getId().equals(businessId))
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber(businessId));
        invoice.setCustomer(customer);
        invoice.setBusiness(business);
        invoice.setCreatedBy(user);
        invoice.setIssuedDate(request.getIssuedDate() != null ? request.getIssuedDate() : new Date());
        invoice.setDueDate(request.getDueDate());
        invoice.setDiscount(request.getDiscount());
        invoice.setPaymentMethod(request.getPaymentMethod());
        invoice.setInternalNote(request.getInternalNote());
        invoice.setStatus("Draft");

        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceItemRequest itemRequest : request.getItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            
            if (itemRequest.getProductId() != null) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .filter(p -> p.getBusiness().getId().equals(businessId))
                        .orElse(null);
                if (product != null) {
                    item.setProduct(product);
                    // Update stock
                    product.setStockLevel(product.getStockLevel() - itemRequest.getQuantity());
                    productRepository.save(product);
                }
            }
            
            item.setDescription(itemRequest.getDescription());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setTotal(itemRequest.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            
            invoice.getItems().add(item);
            subtotal = subtotal.add(item.getTotal());
        }

        invoice.setSubtotal(subtotal);
        BigDecimal discountAmount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal taxableAmount = subtotal.subtract(discountAmount);
        BigDecimal tax = taxableAmount.multiply(request.getTaxRate() != null ? request.getTaxRate() : BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        invoice.setTax(tax);
        invoice.setTotal(taxableAmount.add(tax));

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice updateInvoiceStatus(Long id, String status, Long businessId) {
        Invoice invoice = getInvoiceById(id, businessId);
        invoice.setStatus(status);
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public void deleteInvoice(Long id, Long businessId) {
        Invoice invoice = getInvoiceById(id, businessId);
        invoiceRepository.delete(invoice);
    }

    private String generateInvoiceNumber(Long businessId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(new Date());
        long count = invoiceRepository.count() + 1;
        return String.format("INV-%s-%04d", year, count);
    }

    public BigDecimal getTotalRevenue(Long businessId, Date startDate, Date endDate) {
        List<Invoice> invoices = invoiceRepository.findByBusinessIdAndIssuedDateBetween(businessId, startDate, endDate);
        return invoices.stream()
                .filter(i -> "Paid".equals(i.getStatus()))
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

