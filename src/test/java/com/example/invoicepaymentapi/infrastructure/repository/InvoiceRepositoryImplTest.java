package com.example.invoicepaymentapi.infrastructure.repository;

import com.example.invoicepaymentapi.domain.model.invoice.*;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import com.example.invoicepaymentapi.infrastructure.entity.InvoiceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 請求書リポジトリ実装の単体テスト
 */
@ExtendWith(MockitoExtension.class)
class InvoiceRepositoryImplTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<InvoiceEntity> invoiceQuery;

    @Mock
    private TypedQuery<Long> countQuery;

    @InjectMocks
    private InvoiceRepositoryImpl invoiceRepository;

    private Invoice testInvoice;
    private InvoiceEntity testEntity;

    @BeforeEach
    void setUp() {
        UserId userId = UserId.reconstruct(1);
        IssueDate issueDate = IssueDate.reconstruct(LocalDate.now());
        PaymentAmount paymentAmount = PaymentAmount.reconstruct(new BigDecimal("10000.00"));
        Fee fee = Fee.reconstruct(new BigDecimal("100.00"));
        FeeRate feeRate = FeeRate.reconstruct(new BigDecimal("0.01"));
        TaxAmount taxAmount = TaxAmount.reconstruct(new BigDecimal("10.00"));
        TaxRate taxRate = TaxRate.reconstruct(new BigDecimal("0.10"));
        TotalAmount totalAmount = TotalAmount.reconstruct(new BigDecimal("10110.00"));
        PaymentDueDate paymentDueDate = PaymentDueDate.reconstruct(LocalDate.now().plusDays(30));
        LocalDateTime now = LocalDateTime.now();

        testInvoice = Invoice.reconstruct(
                null,
                userId,
                issueDate,
                paymentAmount,
                fee,
                feeRate,
                taxAmount,
                taxRate,
                totalAmount,
                paymentDueDate,
                now,
                now
        );

        testEntity = new InvoiceEntity();
        testEntity.setId(1);
        testEntity.setUserId(1);
        testEntity.setIssueDate(LocalDate.now());
        testEntity.setPaymentAmount(new BigDecimal("10000.00"));
        testEntity.setFee(new BigDecimal("100.00"));
        testEntity.setFeeRate(new BigDecimal("0.01"));
        testEntity.setTaxAmount(new BigDecimal("10.00"));
        testEntity.setTaxRate(new BigDecimal("0.10"));
        testEntity.setTotalAmount(new BigDecimal("10110.00"));
        testEntity.setPaymentDueDate(LocalDate.now().plusDays(30));
        testEntity.setCreatedAt(now);
        testEntity.setUpdatedAt(now);
    }

    @Nested
    @DisplayName("saveメソッド")
    class SaveTest {
        @Test
        @DisplayName("新規保存が成功する")
        void shouldSaveNewInvoice() {
            // Given
            Invoice invoice = testInvoice;
            doAnswer(invocation -> {
                InvoiceEntity entity = invocation.getArgument(0);
                entity.setId(1);
                return null;
            }).when(entityManager).persist(any(InvoiceEntity.class));

            // When
            InvoiceId result = invoiceRepository.save(invoice);

            // Then
            assertThat(result.value()).isEqualTo(1);
            verify(entityManager).persist(any(InvoiceEntity.class));
            verify(entityManager).flush();
            verify(entityManager, never()).merge(any(InvoiceEntity.class));
        }

        @Test
        @DisplayName("更新が成功する")
        void shouldUpdateExistingInvoice() {
            // Given
            Invoice invoice = Invoice.reconstruct(
                    InvoiceId.reconstruct(1),
                    UserId.reconstruct(1),
                    IssueDate.reconstruct(LocalDate.now()),
                    PaymentAmount.reconstruct(new BigDecimal("10000.00")),
                    Fee.reconstruct(new BigDecimal("100.00")),
                    FeeRate.reconstruct(new BigDecimal("0.01")),
                    TaxAmount.reconstruct(new BigDecimal("10.00")),
                    TaxRate.reconstruct(new BigDecimal("0.10")),
                    TotalAmount.reconstruct(new BigDecimal("10110.00")),
                    PaymentDueDate.reconstruct(LocalDate.now().plusDays(30)),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            when(entityManager.merge(any(InvoiceEntity.class))).thenReturn(testEntity);

            // When
            InvoiceId result = invoiceRepository.save(invoice);

            // Then
            assertThat(result.value()).isEqualTo(1);
            verify(entityManager).merge(any(InvoiceEntity.class));
            verify(entityManager).flush();
            verify(entityManager, never()).persist(any(InvoiceEntity.class));
        }
    }

    @Nested
    @DisplayName("findByUserIdAndPaymentDueDateBetweenメソッド")
    class FindByUserIdAndPaymentDueDateBetweenTest {
        @Test
        @DisplayName("検索が成功する")
        void shouldFindInvoices() {
            // Given
            UserId userId = UserId.reconstruct(1);
            PaymentDueDate from = PaymentDueDate.reconstruct(LocalDate.now().plusDays(1));
            PaymentDueDate to = PaymentDueDate.reconstruct(LocalDate.now().plusDays(30));
            int offset = 0;
            int limit = 20;

            when(entityManager.createQuery(anyString(), eq(InvoiceEntity.class))).thenReturn(invoiceQuery);
            when(invoiceQuery.setParameter(anyString(), any())).thenReturn(invoiceQuery);
            when(invoiceQuery.setFirstResult(anyInt())).thenReturn(invoiceQuery);
            when(invoiceQuery.setMaxResults(anyInt())).thenReturn(invoiceQuery);
            when(invoiceQuery.getResultList()).thenReturn(Collections.singletonList(testEntity));

            // When
            List<Invoice> result = invoiceRepository.findByUserIdAndPaymentDueDateBetween(
                    userId, from, to, offset, limit
            );

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id().value()).isEqualTo(1);
            verify(invoiceQuery).setParameter("userId", 1);
            verify(invoiceQuery).setParameter("paymentDueFrom", from.value());
            verify(invoiceQuery).setParameter("paymentDueTo", to.value());
            verify(invoiceQuery).setFirstResult(offset);
            verify(invoiceQuery).setMaxResults(limit);
        }

        @Test
        @DisplayName("検索結果が空の場合、空のリストを返す")
        void shouldReturnEmptyListWhenNoResults() {
            // Given
            UserId userId = UserId.reconstruct(1);
            PaymentDueDate from = PaymentDueDate.reconstruct(LocalDate.now().plusDays(1));
            PaymentDueDate to = PaymentDueDate.reconstruct(LocalDate.now().plusDays(30));

            when(entityManager.createQuery(anyString(), eq(InvoiceEntity.class))).thenReturn(invoiceQuery);
            when(invoiceQuery.setParameter(anyString(), any())).thenReturn(invoiceQuery);
            when(invoiceQuery.setFirstResult(anyInt())).thenReturn(invoiceQuery);
            when(invoiceQuery.setMaxResults(anyInt())).thenReturn(invoiceQuery);
            when(invoiceQuery.getResultList()).thenReturn(Collections.emptyList());

            // When
            List<Invoice> result = invoiceRepository.findByUserIdAndPaymentDueDateBetween(
                    userId, from, to, 0, 20
            );

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByUserIdAndPaymentDueDateBetweenメソッド")
    class CountByUserIdAndPaymentDueDateBetweenTest {
        @Test
        @DisplayName("件数取得が成功する")
        void shouldCountInvoices() {
            // Given
            UserId userId = UserId.reconstruct(1);
            PaymentDueDate from = PaymentDueDate.reconstruct(LocalDate.now().plusDays(1));
            PaymentDueDate to = PaymentDueDate.reconstruct(LocalDate.now().plusDays(30));

            when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
            when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
            when(countQuery.getSingleResult()).thenReturn(5L);

            // When
            long result = invoiceRepository.countByUserIdAndPaymentDueDateBetween(userId, from, to);

            // Then
            assertThat(result).isEqualTo(5L);
            verify(countQuery).setParameter("userId", 1);
            verify(countQuery).setParameter("paymentDueFrom", from.value());
            verify(countQuery).setParameter("paymentDueTo", to.value());
        }
    }

    @Nested
    @DisplayName("toEntityメソッド")
    class ToEntityTest {
        @Test
        @DisplayName("ドメインモデルからエンティティへの変換が成功する")
        void shouldConvertDomainToEntity() {
            // Given
            Invoice invoice = Invoice.reconstruct(
                    InvoiceId.reconstruct(1),
                    UserId.reconstruct(1),
                    IssueDate.reconstruct(LocalDate.now()),
                    PaymentAmount.reconstruct(new BigDecimal("10000.00")),
                    Fee.reconstruct(new BigDecimal("100.00")),
                    FeeRate.reconstruct(new BigDecimal("0.01")),
                    TaxAmount.reconstruct(new BigDecimal("10.00")),
                    TaxRate.reconstruct(new BigDecimal("0.10")),
                    TotalAmount.reconstruct(new BigDecimal("10110.00")),
                    PaymentDueDate.reconstruct(LocalDate.now().plusDays(30)),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            when(entityManager.merge(any(InvoiceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            invoiceRepository.save(invoice);

            // Then
            ArgumentCaptor<InvoiceEntity> captor = ArgumentCaptor.forClass(InvoiceEntity.class);
            verify(entityManager).merge(captor.capture());
            InvoiceEntity entity = captor.getValue();
            assertThat(entity.getId()).isEqualTo(1);
            assertThat(entity.getUserId()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("toDomainメソッド")
    class ToDomainTest {
        @Test
        @DisplayName("エンティティからドメインモデルへの変換が成功する")
        void shouldConvertEntityToDomain() {
            // Given
            when(entityManager.createQuery(anyString(), eq(InvoiceEntity.class))).thenReturn(invoiceQuery);
            when(invoiceQuery.setParameter(anyString(), any())).thenReturn(invoiceQuery);
            when(invoiceQuery.setFirstResult(anyInt())).thenReturn(invoiceQuery);
            when(invoiceQuery.setMaxResults(anyInt())).thenReturn(invoiceQuery);
            when(invoiceQuery.getResultList()).thenReturn(Collections.singletonList(testEntity));

            // When
            List<Invoice> result = invoiceRepository.findByUserIdAndPaymentDueDateBetween(
                    UserId.reconstruct(1),
                    PaymentDueDate.reconstruct(LocalDate.now().plusDays(1)),
                    PaymentDueDate.reconstruct(LocalDate.now().plusDays(30)),
                    0,
                    20
            );

            // Then
            assertThat(result).hasSize(1);
            Invoice invoice = result.get(0);
            assertThat(invoice.id().value()).isEqualTo(1);
            assertThat(invoice.userId().value()).isEqualTo(1);
        }
    }
}

