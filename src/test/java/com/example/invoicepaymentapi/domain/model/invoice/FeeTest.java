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
 * 手数料値オブジェクトの単体テスト
 */
class FeeTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("支払金額と手数料率から手数料を作成できる")
        void shouldCreateFeeFromPaymentAmountAndFeeRate() {
            // Given
            PaymentAmount paymentAmount = PaymentAmount.create(new BigDecimal("10000.00"));
            FeeRate feeRate = FeeRate.fixed();

            // When
            Fee fee = Fee.create(paymentAmount, feeRate);

            // Then
            assertNotNull(fee);
            assertEquals(new BigDecimal("400.00"), fee.value());
        }

        @Test
        @DisplayName("有効な手数料で値オブジェクトを作成できる")
        void shouldCreateFeeWithValidValue() {
            // Given
            BigDecimal value = new BigDecimal("400.00");

            // When
            Fee fee = Fee.create(value);

            // Then
            assertNotNull(fee);
            assertEquals(new BigDecimal("400.00"), fee.value());
        }

        @Test
        @DisplayName("0で手数料を作成できる")
        void shouldCreateFeeWithZero() {
            // Given
            BigDecimal value = BigDecimal.ZERO;

            // When
            Fee fee = Fee.create(value);

            // Then
            assertNotNull(fee);
            assertEquals(0, fee.value().compareTo(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("小数部が3桁以上の場合は2桁に丸められる")
        void shouldRoundToTwoDecimalPlaces() {
            // Given
            BigDecimal value = new BigDecimal("400.999");

            // When
            Fee fee = Fee.create(value);

            // Then
            assertNotNull(fee);
            assertEquals(new BigDecimal("401.00"), fee.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な値の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidValue() {
            // Given
            BigDecimal value = new BigDecimal("400.00");

            // When
            List<ValidationError> errors = Fee.validate(value);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な値で値オブジェクトを作成できる")
        void shouldReconstructFeeWithValidValue() {
            // Given
            BigDecimal value = new BigDecimal("400.999");

            // When
            Fee fee = Fee.reconstruct(value);

            // Then
            assertNotNull(fee);
            assertEquals(new BigDecimal("401.00"), fee.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("負の値で手数料を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingFeeWithNegativeValue() {
            // Given
            BigDecimal value = new BigDecimal("-100.00");

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> Fee.create(value)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("fee", exception.getErrors().get(0).field());
            assertEquals("validation.negative", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("nullで手数料を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingFeeWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> Fee.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("fee", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("小数部が4桁以上でも丸め込まれて有効な値になる場合はエラーを返さない")
        void shouldNotReturnErrorWhenValidatingWithMoreThanThreeDecimalPlaces() {
            // Given
            BigDecimal value = new BigDecimal("400.9999"); // 4桁だが、丸め込むと401.00になる

            // When
            List<ValidationError> errors = Fee.validate(value);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("整数部が13桁を超える場合にvalidateメソッドがエラーを返す")
        void shouldReturnErrorWhenValidatingWithIntegerPartExceeding13Digits() {
            // Given
            BigDecimal value = new BigDecimal("99999999999999.00"); // 14桁

            // When
            List<ValidationError> errors = Fee.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("fee", errors.get(0).field());
            assertEquals("validation.maxIntegerDigits", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = Fee.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("fee", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが負の値の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValue() {
            // Given
            BigDecimal value = new BigDecimal("-100.00");

            // When
            List<ValidationError> errors = Fee.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("fee", errors.get(0).field());
            assertEquals("validation.negative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("丸め込み前の値が負で、丸め込み後は0以上になる場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValueThatRoundsToZero() {
            // Given
            BigDecimal value = new BigDecimal("-0.001"); // 丸め込むと0.00になるが、丸め込み前が負なのでエラー

            // When
            List<ValidationError> errors = Fee.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("fee", errors.get(0).field());
            assertEquals("validation.negative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("小数部がある負の値でエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValueWithDecimalPlaces() {
            // Given
            BigDecimal value = new BigDecimal("-0.999"); // 丸め込むと-1.00になる

            // When
            List<ValidationError> errors = Fee.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("fee", errors.get(0).field());
            assertEquals("validation.negative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("reconstructメソッドでnullを渡すとIllegalArgumentExceptionがスローされる")
        void shouldThrowIllegalArgumentExceptionWhenReconstructingWithNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Fee.reconstruct(null)
            );
            assertEquals("Fee cannot be null", exception.getMessage());
        }
    }
}

