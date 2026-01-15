package com.example.azoov_backend.repository;

import com.example.azoov_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBusinessId(Long businessId);

    Optional<Product> findBySkuAndBusinessId(String sku, Long businessId);

    List<Product> findByBusinessIdAndStockLevelLessThanEqual(Long businessId, Integer threshold);

    List<Product> findByBusinessIdAndNameContainingIgnoreCase(Long businessId, String name);
}
