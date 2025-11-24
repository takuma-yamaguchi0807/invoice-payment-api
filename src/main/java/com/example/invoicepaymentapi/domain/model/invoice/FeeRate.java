package com.example.invoicepaymentapi.domain.model.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 手数料率値オブジェクト
 * 固定値: 0.04 (4%)
 * DECIMAL(5,2)に対応
 */
public record FeeRate(BigDecimal value) {
    private static final BigDecimal FIXED_VALUE = new BigDecimal("0.04");
    private static final int SCALE = 2;

    /**
     * 固定値（0.04）のFeeRateを取得
     */
    public static FeeRate fixed() {
        return new FeeRate(FIXED_VALUE);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約のため、nullが来ることはない
     *
     * @param value 手数料率
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static FeeRate reconstruct(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("FeeRate cannot be null");
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new FeeRate(normalized);
    }
}