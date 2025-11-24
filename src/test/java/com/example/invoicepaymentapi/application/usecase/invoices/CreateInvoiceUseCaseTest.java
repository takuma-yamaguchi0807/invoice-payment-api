package com.example.invoicepaymentapi.application.usecase.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceRequestDto;
import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceResponseDto;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.model.invoice.InvoiceId;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 請求書登録ユースケースのテスト
 */
@ExtendWith(MockitoExtension.class)
class CreateInvoiceUseCaseTest {
    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private CreateInvoiceUseCase createInvoiceUseCase;

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("必須項目のみのリクエストで請求書登録が成功する")
        void shouldCreateInvoiceWithRequiredFieldsOnly() {
            // Given
            CreateInvoiceRequestDto requestDto = new CreateInvoiceRequestDto(
                    LocalDate.now().toString(),
                    new BigDecimal("10000.00"),
                    LocalDate.now().plusDays(30).toString()
            );
            when(invoiceRepository.save(any())).thenReturn(InvoiceId.reconstruct(1));

            // When
            CreateInvoiceResponseDto response = createInvoiceUseCase.execute(1, requestDto);

            // Then
            assertThat(response.id()).isEqualTo(1);
        }

        @Test
        @DisplayName("すべての項目を含めたリクエストで請求書登録が成功する")
        void shouldCreateInvoiceWithAllFields() {
            // Given
            CreateInvoiceRequestDto requestDto = new CreateInvoiceRequestDto(
                    LocalDate.now().toString(),
                    new BigDecimal("10000.00"),
                    LocalDate.now().plusDays(30).toString()
            );
            when(invoiceRepository.save(any())).thenReturn(InvoiceId.reconstruct(1));

            // When
            CreateInvoiceResponseDto response = createInvoiceUseCase.execute(1, requestDto);

            // Then
            assertThat(response.id()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("必須項目が不足している場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenRequiredFieldsAreMissing() {
            // Given - issueDateがnull
            CreateInvoiceRequestDto requestDto = new CreateInvoiceRequestDto(
                    null,
                    new BigDecimal("10000.00"),
                    LocalDate.now().plusDays(30).toString()
            );

            // When & Then
            assertThatThrownBy(() -> createInvoiceUseCase.execute(1, requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        @DisplayName("発行日が未来の場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenIssueDateIsFuture() {
            // Given
            CreateInvoiceRequestDto requestDto = new CreateInvoiceRequestDto(
                    LocalDate.now().plusDays(1).toString(),
                    new BigDecimal("10000.00"),
                    LocalDate.now().plusDays(30).toString()
            );

            // When & Then
            assertThatThrownBy(() -> createInvoiceUseCase.execute(1, requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        @DisplayName("支払期日が過去または今日の場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenPaymentDueDateIsNotFuture() {
            // Given
            CreateInvoiceRequestDto requestDto = new CreateInvoiceRequestDto(
                    LocalDate.now().toString(),
                    new BigDecimal("10000.00"),
                    LocalDate.now().toString()
            );

            // When & Then
            assertThatThrownBy(() -> createInvoiceUseCase.execute(1, requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        @DisplayName("支払金額が0以下の場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenPaymentAmountIsZeroOrNegative() {
            // Given
            CreateInvoiceRequestDto requestDto = new CreateInvoiceRequestDto(
                    LocalDate.now().toString(),
                    new BigDecimal("0"),
                    LocalDate.now().plusDays(30).toString()
            );

            // When & Then
            assertThatThrownBy(() -> createInvoiceUseCase.execute(1, requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }
    }
}

