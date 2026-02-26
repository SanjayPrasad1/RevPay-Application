package com.revpay.repository;

import com.revpay.entity.Invoice;
import com.revpay.entity.User;
import com.revpay.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByBusinessUserOrderByCreatedAtDesc(User businessUser);

    List<Invoice> findByBusinessUserAndStatusOrderByCreatedAtDesc(User businessUser, InvoiceStatus status);

    List<Invoice> findByBusinessUserAndStatusNotOrderByCreatedAtDesc(User businessUser, InvoiceStatus status);

    // count invoices by status
    long countByBusinessUserAndStatus(User businessUser, InvoiceStatus status);

    // sum of paid invoice amounts
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i " +
            "WHERE i.businessUser = :user AND i.status = 'PAID'")
    BigDecimal getTotalPaidAmount(@Param("user") User user);

    // sum of outstanding invoice amounts (SENT or OVERDUE)
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i " +
            "WHERE i.businessUser = :user AND i.status IN ('SENT', 'OVERDUE')")
    BigDecimal getTotalOutstandingAmount(@Param("user") User user);
}