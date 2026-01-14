package com.example.azoov_backend.controller;

import com.minierp.dto.ProductRequest;
import com.minierp.model.Product;
import com.minierp.model.User;
import com.minierp.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(productService.getAllProducts(user.getBusiness().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(productService.getProductById(id, user.getBusiness().getId()));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(productService.createProduct(request, user.getBusiness().getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(productService.updateProduct(id, request, user.getBusiness().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, @AuthenticationPrincipal User user) {
        productService.deleteProduct(id, user.getBusiness().getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(productService.getLowStockProducts(user.getBusiness().getId()));
    }
}

