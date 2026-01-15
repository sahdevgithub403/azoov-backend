package com.example.azoov_backend.service;

import com.example.azoov_backend.dto.ProductRequest;
import com.example.azoov_backend.model.Business;
import com.example.azoov_backend.model.Product;
import com.example.azoov_backend.repository.BusinessRepository;
import com.example.azoov_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;

    public List<Product> getAllProducts(Long businessId) {
        return productRepository.findByBusinessId(businessId);
    }

    public Product getProductById(Long id, Long businessId) {
        return productRepository.findById(id)
                .filter(p -> p.getBusiness().getId().equals(businessId))
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public Product createProduct(ProductRequest request, Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStockLevel(request.getStockLevel());
        product.setLowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 10);
        product.setImage(request.getImage());
        product.setBusiness(business);

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request, Long businessId) {
        Product product = getProductById(id, businessId);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStockLevel(request.getStockLevel());
        product.setLowStockThreshold(request.getLowStockThreshold());
        product.setImage(request.getImage());

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id, Long businessId) {
        Product product = getProductById(id, businessId);
        productRepository.delete(product);
    }

    public List<Product> getLowStockProducts(Long businessId) {
        List<Product> products = productRepository.findByBusinessId(businessId);
        int threshold = products.stream()
                .mapToInt(p -> p.getLowStockThreshold() != null ? p.getLowStockThreshold() : 10)
                .max()
                .orElse(10);
        return productRepository.findByBusinessIdAndStockLevelLessThanEqual(businessId, threshold);
    }
}

