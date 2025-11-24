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
 * 請求金額値オブジェクトの単体テスト
 */
class TotalAmountTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("支払金額・手数料・消費税から請求金額を作成できる")
        void shouldCreateTotalAmountFromPaymentAmountFeeAndTaxAmount() {
            // Given
            PaymentAmount paymentAmount = PaymentAmount.create(new BigDecimal("10000.00"));
            Fee fee = Fee.create(new BigDecimal("400.00"));
            TaxAmount taxAmount = TaxAmount.create(new BigDecimal("40.00"));

            // When
            TotalAmount totalAmount = TotalAmount.create(paymentAmount, fee, taxAmount);

            // Then
            assertNotNull(totalAmount);
            assertEquals(new BigDecimal("10440.00"), totalAmount.value());
        }

        @Test
        @DisplayName("有効な請求金額で値オブジェクトを作成できる")
        void shouldCreateTotalAmountWithValidValue() {
            // Given
            BigDecimal value = new BigDecimal("10440.00");

            // When
            TotalAmount totalAmount = TotalAmount.create(value);

            // Then
            assertNotNull(totalAmount);
            assertEquals(new BigDecimal("10440.00"), totalAmount.value());
        }

        @Test
        @DisplayName("0で請求金額を作成できる")
        void shouldCreateTotalAmountWithZero() {
            // Given
            BigDecimal value = BigDecimal.ZERO;

            // When
            TotalAmount totalAmount = TotalAmount.create(value);

            // Then
            assertNotNull(totalAmount);
            assertEquals(0, totalAmount.value().compareTo(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("小数部が3桁以上の場合は2桁に丸められる")
        void shouldRoundToTwoDecimalPlaces() {
            // Given
            BigDecimal value = new BigDecimal("10440.999");

            // When
            TotalAmount totalAmount = TotalAmount.create(value);

            // Then
            assertNotNull(totalAmount);
            assertEquals(new BigDecimal("10441.00"), totalAmount.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な値の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidValue() {
            // Given
            BigDecimal value = new BigDecimal("10440.00");

            // When
            List<ValidationError> errors = TotalAmount.validate(value);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な値で値オブジェクトを作成できる")
        void shouldReconstructTotalAmountWithValidValue() {
            // Given
            BigDecimal value = new BigDecimal("10440.999");

            // When
            TotalAmount totalAmount = TotalAmount.reconstruct(value);

            // Then
            assertNotNull(totalAmount);
            assertEquals(new BigDecimal("10441.00"), totalAmount.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("負の値で請求金額を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingTotalAmountWithNegativeValue() {
            // Given
            BigDecimal value = new BigDecimal("-100.00");

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> TotalAmount.create(value)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("totalAmount", exception.getErrors().get(0).field());
            assertEquals("validation.totalAmount.negative", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("nullで請求金額を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingTotalAmountWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> TotalAmount.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("totalAmount", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("小数部が4桁以上でも丸め込まれて有効な値になる場合はエラーを返さない")
        void shouldNotReturnErrorWhenValidatingWithMoreThanThreeDecimalPlaces() {
            // Given
            BigDecimal value = new BigDecimal("10440.9999"); // 4桁だが、丸め込むと10441.00になる

            // When
            List<ValidationError> errors = TotalAmount.validate(value);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("整数部が13桁を超える場合にvalidateメソッドがエラーを返す")
        void shouldReturnErrorWhenValidatingWithIntegerPartExceeding13Digits() {
            // Given
            BigDecimal value = new BigDecimal("99999999999999.00"); // 14桁

            // When
            List<ValidationError> errors = TotalAmount.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("totalAmount", errors.get(0).field());
            assertEquals("validation.totalAmount.scale", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = TotalAmount.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("totalAmount", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが負の値の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValue() {
            // Given
            BigDecimal value = new BigDecimal("-100.00");

            // When
            List<ValidationError> errors = TotalAmount.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("totalAmount", errors.get(0).field());
            assertEquals("validation.totalAmount.negative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("丸め込み前の値が負で、丸め込み後は0以上になる場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValueThatRoundsToZero() {
            // Given
            BigDecimal value = new BigDecimal("-0.001"); // 丸め込むと0.00になるが、丸め込み前が負なのでエラー

            // When
            List<ValidationError> errors = TotalAmount.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("totalAmount", errors.get(0).field());
            assertEquals("validation.totalAmount.negative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("小数部がある負の値でエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValueWithDecimalPlaces() {
            // Given
            BigDecimal value = new BigDecimal("-0.999"); // 丸め込むと-1.00になる

            // When
            List<ValidationError> errors = TotalAmount.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("totalAmount", errors.get(0).field());
            assertEquals("validation.totalAmount.negative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("reconstructメソッドでnullを渡すとIllegalArgumentExceptionがスローされる")
        void shouldThrowIllegalArgumentExceptionWhenReconstructingWithNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> TotalAmount.reconstruct(null)
            );
            assertEquals("TotalAmount cannot be null", exception.getMessage());
        }
    }
}

