package com.example.invoicepaymentapi.domain.model.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 消費税率値オブジェクト
 * 固定値: 0.10 (10%)
 * DECIMAL(5,2)に対応
 */
public record TaxRate(BigDecimal value) {
    private static final BigDecimal FIXED_VALUE = new BigDecimal("0.10");
    private static final int SCALE = 2;

    /**
     * 固定値（0.10）のTaxRateを取得
     */
    public static TaxRate fixed() {
        return new TaxRate(FIXED_VALUE);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約のため、nullが来ることはない
     *
     * @param value 消費税率
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static TaxRate reconstruct(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("TaxRate cannot be null");
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new TaxRate(normalized);
    }
}
