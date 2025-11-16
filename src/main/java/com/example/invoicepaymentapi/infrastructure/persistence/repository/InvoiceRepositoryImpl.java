package com.example.invoicepaymentapi.infrastructure.persistence.repository;

import com.example.invoicepaymentapi.domain.model.invoice.*;
import com.example.invoicepaymentapi.domain.repository.InvoiceRepository;
import com.example.invoicepaymentapi.infrastructure.persistence.entity.InvoiceEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

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
        return InvoiceId.ofGet(entity.getId());
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
}

