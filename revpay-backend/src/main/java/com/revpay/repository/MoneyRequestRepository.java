package com.revpay.repository;

import com.revpay.entity.MoneyRequest;
import com.revpay.entity.User;
import com.revpay.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {

    // requests coming in to this user
    List<MoneyRequest> findByRecipientOrderByCreatedAtDesc(User recipient);

    // requests this user sent out
    List<MoneyRequest> findByRequesterOrderByCreatedAtDesc(User requester);

    // pending requests coming in
    List<MoneyRequest> findByRecipientAndStatus(User recipient, RequestStatus status);

    // get pending requests where this user is the recipient
    @Query("SELECT COALESCE(SUM(m.amount), 0) FROM MoneyRequest m " +
            "WHERE m.recipient = :user AND m.status = 'PENDING'")
    BigDecimal getTotalPendingAmount(@Param("user") User user);

    long countByRecipientAndStatus(User recipient, RequestStatus status);
}