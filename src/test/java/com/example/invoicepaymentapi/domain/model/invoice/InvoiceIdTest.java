package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 請求書ID値オブジェクトの単体テスト
 */
class InvoiceIdTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効な請求書ID（1以上）で値オブジェクトを作成できる")
        void shouldCreateInvoiceIdWithValidValue() {
            // Given
            Integer invoiceId = 1;

            // When
            InvoiceId invoiceIdVo = InvoiceId.create(invoiceId);

            // Then
            assertNotNull(invoiceIdVo);
            assertEquals(1, invoiceIdVo.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な値の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidValue() {
            // When
            List<ValidationError> errors = InvoiceId.validate(1);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドでnullでも値オブジェクトを作成できる")
        void shouldReconstructInvoiceIdWithNull() {
            // When
            InvoiceId invoiceId = InvoiceId.reconstruct(null);

            // Then
            assertNotNull(invoiceId);
            assertNull(invoiceId.value());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な値で値オブジェクトを作成できる")
        void shouldReconstructInvoiceIdWithValidValue() {
            // Given
            Integer value = 1;

            // When
            InvoiceId invoiceId = InvoiceId.reconstruct(value);

            // Then
            assertNotNull(invoiceId);
            assertEquals(1, invoiceId.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("0で請求書IDを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingInvoiceIdWithZero() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> InvoiceId.create(0)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("invoiceId", exception.getErrors().get(0).field());
            assertEquals("validation.invoiceId.zeroOrNegative", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("負の値で請求書IDを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingInvoiceIdWithNegativeValue() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> InvoiceId.create(-1)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("invoiceId", exception.getErrors().get(0).field());
            assertEquals("validation.invoiceId.zeroOrNegative", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("nullで請求書IDを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingInvoiceIdWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> InvoiceId.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("invoiceId", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = InvoiceId.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("invoiceId", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが0の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingZero() {
            // When
            List<ValidationError> errors = InvoiceId.validate(0);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("invoiceId", errors.get(0).field());
            assertEquals("validation.invoiceId.zeroOrNegative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドが負の値の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValue() {
            // When
            List<ValidationError> errors = InvoiceId.validate(-1);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("invoiceId", errors.get(0).field());
            assertEquals("validation.invoiceId.zeroOrNegative", errors.get(0).messageKey());
        }
    }
}
