package com.example.azoov_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class InvoiceRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Date issuedDate;
    private Date dueDate;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal taxRate = BigDecimal.ZERO;
    private String paymentMethod;
    private String internalNote;

    @Valid
    @NotNull(message = "Invoice items are required")
    private List<InvoiceItemRequest> items;
}

