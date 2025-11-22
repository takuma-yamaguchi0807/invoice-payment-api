package com.example.invoicepaymentapi.domain.model.invoice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 手数料率値オブジェクトの単体テスト
 */
class FeeRateTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("fixedメソッドで固定値（0.04）のFeeRateを取得できる")
        void shouldGetFixedFeeRate() {
            // When
            FeeRate feeRate = FeeRate.fixed();

            // Then
            assertNotNull(feeRate);
            assertEquals(new BigDecimal("0.04"), feeRate.value());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な値で値オブジェクトを作成できる")
        void shouldReconstructFeeRateWithValidValue() {
            // Given
            BigDecimal value = new BigDecimal("0.04");

            // When
            FeeRate feeRate = FeeRate.reconstruct(value);

            // Then
            assertNotNull(feeRate);
            assertEquals(new BigDecimal("0.04"), feeRate.value());
        }

        @Test
        @DisplayName("reconstructメソッドで小数部が3桁以上の場合は2桁に丸められる")
        void shouldRoundToTwoDecimalPlacesInReconstruct() {
            // Given
            BigDecimal value = new BigDecimal("0.045");

            // When
            FeeRate feeRate = FeeRate.reconstruct(value);

            // Then
            assertNotNull(feeRate);
            assertEquals(new BigDecimal("0.05"), feeRate.value());
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
                    () -> FeeRate.reconstruct(null)
            );
            assertEquals("FeeRate cannot be null", exception.getMessage());
        }
    }
}

