package com.revpay.repository;

import com.revpay.entity.Transaction;
import com.revpay.entity.User;
import com.revpay.enums.TransactionStatus;
import com.revpay.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // get all transactions where user is sender or receiver
    @Query("SELECT t FROM Transaction t WHERE t.sender = :user OR t.receiver = :user ORDER BY t.createdAt DESC")
    List<Transaction> findAllByUser(@Param("user") User user);

    List<Transaction> findBySenderOrderByCreatedAtDesc(User sender);

    List<Transaction> findByReceiverOrderByCreatedAtDesc(User receiver);

    // filter transactions with optional parameters
    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.sender = :user OR t.receiver = :user) AND " +
            "(:type IS NULL OR t.type = :type) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR t.createdAt <= :endDate) AND " +
            "(:minAmount IS NULL OR t.amount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR t.amount <= :maxAmount) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findWithFilters(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount
    );

    // search by sender or receiver name
    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.sender = :user OR t.receiver = :user) AND " +
            "(LOWER(t.sender.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.receiver.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> searchByName(
            @Param("user") User user,
            @Param("keyword") String keyword
    );

    // get all received transactions for a user within a date range
    @Query("SELECT t FROM Transaction t WHERE t.receiver = :user " +
            "AND t.status = 'COMPLETED' AND t.createdAt >= :startDate " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findReceivedAfter(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate
    );

    // get total amount received by a user
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.receiver = :user AND t.status = 'COMPLETED'")
    BigDecimal getTotalReceived(@Param("user") User user);

    // get total amount sent by a user
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.sender = :user AND t.status = 'COMPLETED' " +
            "AND t.type = 'SENT'")
    BigDecimal getTotalSent(@Param("user") User user);

    // get total received within date range
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.receiver = :user AND t.status = 'COMPLETED' " +
            "AND t.createdAt >= :startDate")
    BigDecimal getTotalReceivedAfter(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate
    );

    // get top senders to this user (top customers)
    @Query("SELECT t.sender.fullName, t.sender.email, " +
            "SUM(t.amount) as totalVolume, COUNT(t) as txCount " +
            "FROM Transaction t WHERE t.receiver = :user " +
            "AND t.status = 'COMPLETED' AND t.sender IS NOT NULL " +
            "GROUP BY t.sender.fullName, t.sender.email " +
            "ORDER BY totalVolume DESC")
    List<Object[]> findTopCustomers(@Param("user") User user);


}