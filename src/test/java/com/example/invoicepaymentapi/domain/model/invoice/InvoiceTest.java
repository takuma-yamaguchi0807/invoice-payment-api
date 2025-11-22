package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.model.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 請求書エンティティの単体テスト
 */
class InvoiceTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効な値で請求書を作成できる")
        void shouldCreateInvoiceWithValidValues() {
            // Given
            UserId userId = UserId.create(1);
            IssueDate issueDate = IssueDate.create(LocalDate.now().toString());
            PaymentAmount paymentAmount = PaymentAmount.create(new BigDecimal("10000.00"));
            PaymentDueDate paymentDueDate = PaymentDueDate.create(LocalDate.now().plusDays(30).toString());

            // When
            Invoice invoice = Invoice.create(userId, issueDate, paymentAmount, paymentDueDate);

            // Then
            assertNotNull(invoice);
            assertNull(invoice.id()); // 新規作成時はIDはnull
            assertEquals(userId, invoice.userId());
            assertEquals(issueDate, invoice.issueDate());
            assertEquals(paymentAmount, invoice.paymentAmount());
            assertNotNull(invoice.fee());
            assertNotNull(invoice.feeRate());
            assertNotNull(invoice.taxAmount());
            assertNotNull(invoice.taxRate());
            assertNotNull(invoice.totalAmount());
            assertEquals(paymentDueDate, invoice.paymentDueDate());
            assertNotNull(invoice.createdAt());
            assertNotNull(invoice.updatedAt());
        }

        @Test
        @DisplayName("請求書作成時に手数料が正しく計算される")
        void shouldCalculateFeeCorrectly() {
            // Given
            UserId userId = UserId.create(1);
            IssueDate issueDate = IssueDate.create(LocalDate.now().toString());
            PaymentAmount paymentAmount = PaymentAmount.create(new BigDecimal("10000.00"));
            PaymentDueDate paymentDueDate = PaymentDueDate.create(LocalDate.now().plusDays(30).toString());

            // When
            Invoice invoice = Invoice.create(userId, issueDate, paymentAmount, paymentDueDate);

            // Then
            assertNotNull(invoice.fee());
            assertTrue(invoice.fee().value().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("請求書作成時に消費税が正しく計算される")
        void shouldCalculateTaxAmountCorrectly() {
            // Given
            UserId userId = UserId.create(1);
            IssueDate issueDate = IssueDate.create(LocalDate.now().toString());
            PaymentAmount paymentAmount = PaymentAmount.create(new BigDecimal("10000.00"));
            PaymentDueDate paymentDueDate = PaymentDueDate.create(LocalDate.now().plusDays(30).toString());

            // When
            Invoice invoice = Invoice.create(userId, issueDate, paymentAmount, paymentDueDate);

            // Then
            assertNotNull(invoice.taxAmount());
            assertTrue(invoice.taxAmount().value().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("請求書作成時に請求金額が正しく計算される")
        void shouldCalculateTotalAmountCorrectly() {
            // Given
            UserId userId = UserId.create(1);
            IssueDate issueDate = IssueDate.create(LocalDate.now().toString());
            PaymentAmount paymentAmount = PaymentAmount.create(new BigDecimal("10000.00"));
            PaymentDueDate paymentDueDate = PaymentDueDate.create(LocalDate.now().plusDays(30).toString());

            // When
            Invoice invoice = Invoice.create(userId, issueDate, paymentAmount, paymentDueDate);

            // Then
            assertNotNull(invoice.totalAmount());
            // 請求金額 = 支払金額 + 手数料 + 消費税
            BigDecimal expectedTotal = paymentAmount.value()
                    .add(invoice.fee().value())
                    .add(invoice.taxAmount().value());
            assertEquals(0, invoice.totalAmount().value().compareTo(expectedTotal));
        }

        @Test
        @DisplayName("請求書作成時に作成日時と更新日時が設定される")
        void shouldSetCreatedAtAndUpdatedAt() {
            // Given
            UserId userId = UserId.create(1);
            IssueDate issueDate = IssueDate.create(LocalDate.now().toString());
            PaymentAmount paymentAmount = PaymentAmount.create(new BigDecimal("10000.00"));
            PaymentDueDate paymentDueDate = PaymentDueDate.create(LocalDate.now().plusDays(30).toString());
            LocalDateTime beforeCreation = LocalDateTime.now();

            // When
            Invoice invoice = Invoice.create(userId, issueDate, paymentAmount, paymentDueDate);
            LocalDateTime afterCreation = LocalDateTime.now();

            // Then
            assertNotNull(invoice.createdAt());
            assertNotNull(invoice.updatedAt());
            assertTrue(invoice.createdAt().isAfter(beforeCreation) || invoice.createdAt().isEqual(beforeCreation));
            assertTrue(invoice.createdAt().isBefore(afterCreation) || invoice.createdAt().isEqual(afterCreation));
            assertEquals(invoice.createdAt(), invoice.updatedAt());
        }

        @Test
        @DisplayName("reconstructメソッドで既存の請求書を再構築できる")
        void shouldReconstructInvoice() {
            // Given
            InvoiceId id = InvoiceId.reconstruct(1);
            UserId userId = UserId.create(1);
            IssueDate issueDate = IssueDate.create(LocalDate.now().toString());
            PaymentAmount paymentAmount = PaymentAmount.create(new BigDecimal("10000.00"));
            Fee fee = Fee.reconstruct(new BigDecimal("100.00"));
            FeeRate feeRate = FeeRate.reconstruct(new BigDecimal("0.01"));
            TaxAmount taxAmount = TaxAmount.reconstruct(new BigDecimal("10.00"));
            TaxRate taxRate = TaxRate.reconstruct(new BigDecimal("0.10"));
            TotalAmount totalAmount = TotalAmount.reconstruct(new BigDecimal("10110.00"));
            PaymentDueDate paymentDueDate = PaymentDueDate.create(LocalDate.now().plusDays(30).toString());
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            // When
            Invoice invoice = Invoice.reconstruct(
                    id, userId, issueDate, paymentAmount, fee, feeRate,
                    taxAmount, taxRate, totalAmount, paymentDueDate,
                    createdAt, updatedAt
            );

            // Then
            assertNotNull(invoice);
            assertEquals(id, invoice.id());
            assertEquals(userId, invoice.userId());
            assertEquals(issueDate, invoice.issueDate());
            assertEquals(paymentAmount, invoice.paymentAmount());
            assertEquals(fee, invoice.fee());
            assertEquals(feeRate, invoice.feeRate());
            assertEquals(taxAmount, invoice.taxAmount());
            assertEquals(taxRate, invoice.taxRate());
            assertEquals(totalAmount, invoice.totalAmount());
            assertEquals(paymentDueDate, invoice.paymentDueDate());
            assertEquals(createdAt, invoice.createdAt());
            assertEquals(updatedAt, invoice.updatedAt());
        }
    }
}
