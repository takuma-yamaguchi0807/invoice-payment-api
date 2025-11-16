package com.example.invoicepaymentapi.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 請求書JPAエンティティ
 * データベーステーブルとマッピング
 */
@Entity
@Table(name = "invoices")
@Getter
@Setter
public class InvoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "payment_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal fee;

    @Column(name = "fee_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal feeRate;

    @Column(name = "tax_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "payment_due_date", nullable = false)
    private LocalDate paymentDueDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

