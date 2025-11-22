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
 * 消費税値オブジェクトの単体テスト
 */
class TaxAmountTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("手数料と消費税率から消費税を作成できる")
        void shouldCreateTaxAmountFromFeeAndTaxRate() {
            // Given
            Fee fee = Fee.create(new BigDecimal("400.00"));
            TaxRate taxRate = TaxRate.fixed();

            // When
            TaxAmount taxAmount = TaxAmount.create(fee, taxRate);

            // Then
            assertNotNull(taxAmount);
            assertEquals(new BigDecimal("40.00"), taxAmount.value());
        }

        @Test
        @DisplayName("有効な消費税で値オブジェクトを作成できる")
        void shouldCreateTaxAmountWithValidValue() {
            // Given
            BigDecimal value = new BigDecimal("40.00");

            // When
            TaxAmount taxAmount = TaxAmount.create(value);

            // Then
            assertNotNull(taxAmount);
            assertEquals(new BigDecimal("40.00"), taxAmount.value());
        }

        @Test
        @DisplayName("0で消費税を作成できる")
        void shouldCreateTaxAmountWithZero() {
            // Given
            BigDecimal value = BigDecimal.ZERO;

            // When
            TaxAmount taxAmount = TaxAmount.create(value);

            // Then
            assertNotNull(taxAmount);
            assertEquals(0, taxAmount.value().compareTo(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("小数部が3桁以上の場合は2桁に丸められる")
        void shouldRoundToTwoDecimalPlaces() {
            // Given
            BigDecimal value = new BigDecimal("40.999");

            // When
            TaxAmount taxAmount = TaxAmount.create(value);

            // Then
            assertNotNull(taxAmount);
            assertEquals(new BigDecimal("41.00"), taxAmount.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な値の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidValue() {
            // Given
            BigDecimal value = new BigDecimal("40.00");

            // When
            List<ValidationError> errors = TaxAmount.validate(value);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な値で値オブジェクトを作成できる")
        void shouldReconstructTaxAmountWithValidValue() {
            // Given
            BigDecimal value = new BigDecimal("40.999");

            // When
            TaxAmount taxAmount = TaxAmount.reconstruct(value);

            // Then
            assertNotNull(taxAmount);
            assertEquals(new BigDecimal("41.00"), taxAmount.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("負の値で消費税を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingTaxAmountWithNegativeValue() {
            // Given
            BigDecimal value = new BigDecimal("-10.00");

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> TaxAmount.create(value)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("taxAmount", exception.getErrors().get(0).field());
            assertEquals("validation.taxAmount.negative", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("nullで消費税を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingTaxAmountWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> TaxAmount.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("taxAmount", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("小数部が4桁以上の場合にvalidateメソッドがエラーを返す")
        void shouldReturnErrorWhenValidatingWithMoreThanThreeDecimalPlaces() {
            // Given
            BigDecimal value = new BigDecimal("40.9999"); // 4桁

            // When
            List<ValidationError> errors = TaxAmount.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("taxAmount", errors.get(0).field());
            assertEquals("validation.taxAmount.scale", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("整数部が13桁を超える場合にvalidateメソッドがエラーを返す")
        void shouldReturnErrorWhenValidatingWithIntegerPartExceeding13Digits() {
            // Given
            BigDecimal value = new BigDecimal("99999999999999.00"); // 14桁

            // When
            List<ValidationError> errors = TaxAmount.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("taxAmount", errors.get(0).field());
            assertEquals("validation.taxAmount.scale", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = TaxAmount.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("taxAmount", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが負の値の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValue() {
            // Given
            BigDecimal value = new BigDecimal("-10.00");

            // When
            List<ValidationError> errors = TaxAmount.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("taxAmount", errors.get(0).field());
            assertEquals("validation.taxAmount.negative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("丸め込み前の値が負で、丸め込み後は0以上になる場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValueThatRoundsToZero() {
            // Given
            BigDecimal value = new BigDecimal("-0.001"); // 丸め込むと0.00になるが、丸め込み前が負なのでエラー

            // When
            List<ValidationError> errors = TaxAmount.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("taxAmount", errors.get(0).field());
            assertEquals("validation.taxAmount.negative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("小数部がある負の値でエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValueWithDecimalPlaces() {
            // Given
            BigDecimal value = new BigDecimal("-0.999"); // 丸め込むと-1.00になる

            // When
            List<ValidationError> errors = TaxAmount.validate(value);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("taxAmount", errors.get(0).field());
            assertEquals("validation.taxAmount.negative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("reconstructメソッドでnullを渡すとIllegalArgumentExceptionがスローされる")
        void shouldThrowIllegalArgumentExceptionWhenReconstructingWithNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> TaxAmount.reconstruct(null)
            );
            assertEquals("TaxAmount cannot be null", exception.getMessage());
        }
    }
}

