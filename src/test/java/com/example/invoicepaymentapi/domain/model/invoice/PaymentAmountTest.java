package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 支払金額値オブジェクトの単体テスト
 */
class PaymentAmountTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効な支払金額で値オブジェクトを作成できる")
        void shouldCreatePaymentAmountWithValidValue() {
            // Given
            BigDecimal amount = new BigDecimal("10000.00");

            // When
            PaymentAmount paymentAmount = PaymentAmount.create(amount);

            // Then
            assertNotNull(paymentAmount);
            assertEquals(new BigDecimal("10000.00"), paymentAmount.value());
        }

        @Test
        @DisplayName("最小値（0.01）で支払金額を作成できる")
        void shouldCreatePaymentAmountWithMinimumValue() {
            // Given
            BigDecimal amount = new BigDecimal("0.01");

            // When
            PaymentAmount paymentAmount = PaymentAmount.create(amount);

            // Then
            assertNotNull(paymentAmount);
            assertEquals(new BigDecimal("0.01"), paymentAmount.value());
        }

        @Test
        @DisplayName("小数部が3桁以上の場合は2桁に丸められる")
        void shouldRoundToTwoDecimalPlaces() {
            // Given
            BigDecimal amount = new BigDecimal("10000.999");

            // When
            PaymentAmount paymentAmount = PaymentAmount.create(amount);

            // Then
            assertNotNull(paymentAmount);
            assertEquals(new BigDecimal("10001.00"), paymentAmount.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な値の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidValue() {
            // Given
            BigDecimal amount = new BigDecimal("10000.00");

            // When
            List<ValidationError> errors = PaymentAmount.validate(amount);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な値で値オブジェクトを作成できる")
        void shouldReconstructPaymentAmountWithValidValue() {
            // Given
            BigDecimal amount = new BigDecimal("10000.999");

            // When
            PaymentAmount paymentAmount = PaymentAmount.reconstruct(amount);

            // Then
            assertNotNull(paymentAmount);
            assertEquals(new BigDecimal("10001.00"), paymentAmount.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("0で支払金額を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPaymentAmountWithZero() {
            // Given
            BigDecimal amount = BigDecimal.ZERO;

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PaymentAmount.create(amount)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentAmount", exception.getErrors().get(0).field());
            assertEquals("validation.paymentAmount.min", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("負の値で支払金額を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPaymentAmountWithNegativeValue() {
            // Given
            BigDecimal amount = new BigDecimal("-100.00");

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PaymentAmount.create(amount)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentAmount", exception.getErrors().get(0).field());
            assertEquals("validation.paymentAmount.min", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("0.01未満で支払金額を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPaymentAmountWithLessThanMinimum() {
            // Given
            BigDecimal amount = new BigDecimal("0.005"); // 丸め込むと0.01未満になる値

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PaymentAmount.create(amount)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentAmount", exception.getErrors().get(0).field());
            assertEquals("validation.paymentAmount.min", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("nullで支払金額を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPaymentAmountWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PaymentAmount.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentAmount", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("小数部が4桁以上でも丸め込まれて有効な値になる場合はエラーを返さない")
        void shouldNotReturnErrorWhenValidatingWithMoreThanThreeDecimalPlaces() {
            // Given
            BigDecimal amount = new BigDecimal("10000.9999"); // 4桁だが、丸め込むと10001.00になる

            // When
            List<ValidationError> errors = PaymentAmount.validate(amount);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("整数部が13桁を超える場合にvalidateメソッドがエラーを返す")
        void shouldReturnErrorWhenValidatingWithIntegerPartExceeding13Digits() {
            // Given
            BigDecimal amount = new BigDecimal("99999999999999.00"); // 14桁

            // When
            List<ValidationError> errors = PaymentAmount.validate(amount);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("paymentAmount", errors.get(0).field());
            assertEquals("validation.paymentAmount.scale", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = PaymentAmount.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("paymentAmount", errors.get(0).field());
        }

        @Test
        @DisplayName("reconstructメソッドでnullを渡すとIllegalArgumentExceptionがスローされる")
        void shouldThrowIllegalArgumentExceptionWhenReconstructingWithNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> PaymentAmount.reconstruct(null)
            );
            assertEquals("PaymentAmount cannot be null", exception.getMessage());
        }
    }
}
