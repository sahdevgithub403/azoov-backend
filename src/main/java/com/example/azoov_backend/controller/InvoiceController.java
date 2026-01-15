package com.example.azoov_backend.controller;

import com.example.azoov_backend.model.Invoice;
import com.example.azoov_backend.model.User;
import com.example.azoov_backend.dto.InvoiceRequest;

import com.example.azoov_backend.service.InvoiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(invoiceService.getAllInvoices(user.getBusiness().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id, user.getBusiness().getId()));
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody InvoiceRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(invoiceService.createInvoice(request, user.getBusiness().getId(), user.getEmail()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Invoice> updateInvoiceStatus(@PathVariable Long id, @RequestParam String status,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(id, status, user.getBusiness().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id, @AuthenticationPrincipal User user) {
        invoiceService.deleteInvoice(id, user.getBusiness().getId());
        return ResponseEntity.noContent().build();
    }
}
