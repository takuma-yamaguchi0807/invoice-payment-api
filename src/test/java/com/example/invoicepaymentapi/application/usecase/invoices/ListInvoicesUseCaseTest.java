package com.example.invoicepaymentapi.application.usecase.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.InvoiceListResponseDto;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.model.invoice.*;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import com.example.invoicepaymentapi.domain.repository.InvoiceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 請求書一覧取得ユースケースのテスト
 */
@ExtendWith(MockitoExtension.class)
class ListInvoicesUseCaseTest {
    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private ListInvoicesUseCase listInvoicesUseCase;

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("クエリパラメータなしで請求書一覧取得が成功する")
        void shouldListInvoicesWithNoQueryParameters() {
            // Given
            when(invoiceRepository.findByUserIdAndPaymentDueDateBetween(any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.countByUserIdAndPaymentDueDateBetween(any(), any(), any()))
                    .thenReturn(0L);

            // When
            InvoiceListResponseDto response = listInvoicesUseCase.execute(UserId.reconstruct(1), null, null, null, null);

            // Then
            assertThat(response.items()).isEmpty();
            assertThat(response.pagination().page_number()).isEqualTo(1);
            assertThat(response.pagination().page_size()).isEqualTo(20);
            assertThat(response.pagination().total()).isEqualTo(0);
            assertThat(response.pagination().total_pages()).isEqualTo(0);
        }

        @Test
        @DisplayName("すべてのクエリパラメータを含めたリクエストで請求書一覧取得が成功する")
        void shouldListInvoicesWithAllQueryParameters() {
            // Given
            LocalDate from = LocalDate.now().plusDays(1); // 未来の日付
            LocalDate to = LocalDate.now().plusDays(30);
            LocalDate issueDate = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();
            
            // Invoice.reconstruct()を使ってID付きの請求書を作成
            Invoice invoice = Invoice.reconstruct(
                    InvoiceId.reconstruct(1),
                    UserId.reconstruct(1),
                    IssueDate.reconstruct(issueDate),
                    PaymentAmount.reconstruct(new BigDecimal("10000.00")),
                    Fee.reconstruct(new BigDecimal("100.00")),
                    FeeRate.fixed(),
                    TaxAmount.reconstruct(new BigDecimal("10.00")),
                    TaxRate.fixed(),
                    TotalAmount.reconstruct(new BigDecimal("10110.00")),
                    PaymentDueDate.reconstruct(to),
                    now,
                    now
            );
            when(invoiceRepository.findByUserIdAndPaymentDueDateBetween(any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(List.of(invoice));
            when(invoiceRepository.countByUserIdAndPaymentDueDateBetween(any(), any(), any()))
                    .thenReturn(1L);

            // When
            InvoiceListResponseDto response = listInvoicesUseCase.execute(UserId.reconstruct(1), from.toString(), to.toString(), 1, 10);

            // Then
            assertThat(response.items()).hasSize(1);
            assertThat(response.items().get(0).id()).isEqualTo(invoice.id().value());
            assertThat(response.pagination().page_number()).isEqualTo(1);
            assertThat(response.pagination().page_size()).isEqualTo(10);
            assertThat(response.pagination().total()).isEqualTo(1);
            assertThat(response.pagination().total_pages()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("日付形式が不正な場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenDateFormatIsInvalid() {
            // When & Then
            assertThatThrownBy(() -> listInvoicesUseCase.execute(UserId.reconstruct(1), "invalid-date", null, null, null))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        @DisplayName("支払期日の終了日が開始日より前の場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenPaymentDueToIsBeforePaymentDueFrom() {
            // Given
            LocalDate from = LocalDate.now().plusDays(30);
            LocalDate to = LocalDate.now().plusDays(1);

            // When & Then
            assertThatThrownBy(() -> listInvoicesUseCase.execute(UserId.reconstruct(1), from.toString(), to.toString(), null, null))
                    .isInstanceOf(DomainValidationException.class);
        }
    }
}

