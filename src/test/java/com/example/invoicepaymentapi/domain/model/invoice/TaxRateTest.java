package com.example.invoicepaymentapi.domain.model.invoice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 消費税率値オブジェクトの単体テスト
 */
class TaxRateTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("fixedメソッドで固定値（0.10）のTaxRateを取得できる")
        void shouldGetFixedTaxRate() {
            // When
            TaxRate taxRate = TaxRate.fixed();

            // Then
            assertNotNull(taxRate);
            assertEquals(new BigDecimal("0.10"), taxRate.value());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な値で値オブジェクトを作成できる")
        void shouldReconstructTaxRateWithValidValue() {
            // Given
            BigDecimal value = new BigDecimal("0.10");

            // When
            TaxRate taxRate = TaxRate.reconstruct(value);

            // Then
            assertNotNull(taxRate);
            assertEquals(new BigDecimal("0.10"), taxRate.value());
        }

        @Test
        @DisplayName("reconstructメソッドで小数部が3桁以上の場合は2桁に丸められる")
        void shouldRoundToTwoDecimalPlacesInReconstruct() {
            // Given
            BigDecimal value = new BigDecimal("0.105");

            // When
            TaxRate taxRate = TaxRate.reconstruct(value);

            // Then
            assertNotNull(taxRate);
            assertEquals(new BigDecimal("0.11"), taxRate.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("reconstructメソッドでnullを渡すとIllegalArgumentExceptionがスローされる")
        void shouldThrowIllegalArgumentExceptionWhenReconstructingWithNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> TaxRate.reconstruct(null)
            );
            assertEquals("TaxRate cannot be null", exception.getMessage());
        }
    }
}

