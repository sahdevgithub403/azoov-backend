package com.example.azoov_backend.repository;

import com.example.azoov_backend.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByBusinessId(Long businessId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByBusinessIdAndIssuedDateBetween(Long businessId, Date startDate, Date endDate);

    @Query("SELECT SUM(i.total) FROM Invoice i WHERE i.business.id = :businessId AND i.status = 'Paid'")
    Double getTotalRevenueByBusinessId(Long businessId);

    long countByBusinessId(Long businessId);
}
