package com.example.invoicepaymentapi.infrastructure.repository;

import com.example.invoicepaymentapi.domain.model.invoice.*;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import com.example.invoicepaymentapi.domain.repository.InvoiceRepository;
import com.example.invoicepaymentapi.infrastructure.entity.InvoiceEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 請求書リポジトリ実装
 * ドメイン層のInvoiceRepositoryインターフェースを実装
 */
@Repository
public class InvoiceRepositoryImpl implements InvoiceRepository {
    private final EntityManager entityManager;

    public InvoiceRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public InvoiceId save(Invoice invoice) {
        InvoiceEntity entity = toEntity(invoice);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        entityManager.flush();
        return InvoiceId.reconstruct(entity.getId());
    }

    @Override
    public List<Invoice> findByUserIdAndPaymentDueDateBetween(
            UserId userId,
            PaymentDueDate paymentDueFrom,
            PaymentDueDate paymentDueTo,
            int offset,
            int limit
    ) {
        TypedQuery<InvoiceEntity> query = entityManager.createQuery(
                "SELECT i FROM InvoiceEntity i " +
                        "WHERE i.userId = :userId " +
                        "AND i.paymentDueDate >= :paymentDueFrom " +
                        "AND i.paymentDueDate <= :paymentDueTo " +
                        "ORDER BY i.paymentDueDate ASC, i.issueDate ASC",
                InvoiceEntity.class
        );
        query.setParameter("userId", userId.value());
        query.setParameter("paymentDueFrom", paymentDueFrom.value());
        query.setParameter("paymentDueTo", paymentDueTo.value());
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUserIdAndPaymentDueDateBetween(
            UserId userId,
            PaymentDueDate paymentDueFrom,
            PaymentDueDate paymentDueTo
    ) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(i) FROM InvoiceEntity i " +
                        "WHERE i.userId = :userId " +
                        "AND i.paymentDueDate >= :paymentDueFrom " +
                        "AND i.paymentDueDate <= :paymentDueTo",
                Long.class
        );
        query.setParameter("userId", userId.value());
        query.setParameter("paymentDueFrom", paymentDueFrom.value());
        query.setParameter("paymentDueTo", paymentDueTo.value());

        return query.getSingleResult();
    }

    /**
     * ドメインモデルからJPAエンティティに変換
     */
    private InvoiceEntity toEntity(Invoice invoice) {
        InvoiceEntity entity = new InvoiceEntity();
        if (invoice.id() != null) {
            entity.setId(invoice.id().value());
        }
        entity.setUserId(invoice.userId().value());
        entity.setIssueDate(invoice.issueDate().value());
        entity.setPaymentAmount(invoice.paymentAmount().value());
        entity.setFee(invoice.fee().value());
        entity.setFeeRate(invoice.feeRate().value());
        entity.setTaxAmount(invoice.taxAmount().value());
        entity.setTaxRate(invoice.taxRate().value());
        entity.setTotalAmount(invoice.totalAmount().value());
        entity.setPaymentDueDate(invoice.paymentDueDate().value());
        entity.setCreatedAt(invoice.createdAt());
        entity.setUpdatedAt(invoice.updatedAt());
        return entity;
    }

    /**
     * JPAエンティティからドメインモデルに変換
     */
    private Invoice toDomain(InvoiceEntity entity) {
        return Invoice.reconstruct(
                InvoiceId.reconstruct(entity.getId()),
                UserId.reconstruct(entity.getUserId()),
                IssueDate.reconstruct(entity.getIssueDate()),
                PaymentAmount.reconstruct(entity.getPaymentAmount()),
                Fee.reconstruct(entity.getFee()),
                FeeRate.reconstruct(entity.getFeeRate()),
                TaxAmount.reconstruct(entity.getTaxAmount()),
                TaxRate.reconstruct(entity.getTaxRate()),
                TotalAmount.reconstruct(entity.getTotalAmount()),
                PaymentDueDate.reconstruct(entity.getPaymentDueDate()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

