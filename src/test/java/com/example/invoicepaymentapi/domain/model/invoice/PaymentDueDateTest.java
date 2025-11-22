package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 支払期日値オブジェクトの単体テスト
 */
class PaymentDueDateTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効な未来の日付文字列から支払期日を作成できる")
        void shouldCreatePaymentDueDateFromString() {
            // Given
            String dateString = LocalDate.now().plusDays(30).toString();

            // When
            PaymentDueDate paymentDueDate = PaymentDueDate.create(dateString);

            // Then
            assertNotNull(paymentDueDate);
            assertEquals(LocalDate.parse(dateString), paymentDueDate.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な未来の日付の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingFutureDate() {
            // Given
            String futureDate = LocalDate.now().plusDays(1).toString();

            // When
            List<ValidationError> errors = PaymentDueDate.validate(futureDate);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な日付で値オブジェクトを作成できる")
        void shouldReconstructPaymentDueDateWithValidDate() {
            // Given
            LocalDate date = LocalDate.now().plusDays(30);

            // When
            PaymentDueDate paymentDueDate = PaymentDueDate.reconstruct(date);

            // Then
            assertNotNull(paymentDueDate);
            assertEquals(date, paymentDueDate.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("空文字列で支払期日を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPaymentDueDateWithEmptyString() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PaymentDueDate.create("")
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentDueDate", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("不正な日付形式で支払期日を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPaymentDueDateWithInvalidFormat() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PaymentDueDate.create("2024/01/01")
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentDueDate", exception.getErrors().get(0).field());
            assertEquals("validation.date.format", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = PaymentDueDate.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("paymentDueDate", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが空文字列の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingEmptyString() {
            // When
            List<ValidationError> errors = PaymentDueDate.validate("");

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("paymentDueDate", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが今日の日付の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingToday() {
            // Given
            String today = LocalDate.now().toString();

            // When
            List<ValidationError> errors = PaymentDueDate.validate(today);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("paymentDueDate", errors.get(0).field());
            assertEquals("validation.paymentDueDate.notFuture", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドが過去の日付の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingPastDate() {
            // Given
            String pastDate = LocalDate.now().minusDays(1).toString();

            // When
            List<ValidationError> errors = PaymentDueDate.validate(pastDate);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("paymentDueDate", errors.get(0).field());
            assertEquals("validation.paymentDueDate.notFuture", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("reconstructメソッドでnullを渡すとIllegalArgumentExceptionがスローされる")
        void shouldThrowIllegalArgumentExceptionWhenReconstructingWithNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> PaymentDueDate.reconstruct(null)
            );
            assertEquals("PaymentDueDate cannot be null", exception.getMessage());
        }
    }
}
