package com.example.invoicepaymentapi.domain.model.invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 消費税率値オブジェクト
 * 固定値: 0.10 (10%)
 * DECIMAL(5,2)に対応
 */
public record TaxRate(BigDecimal value) {
    private static final Logger log = LoggerFactory.getLogger(TaxRate.class);
    private static final BigDecimal FIXED_VALUE = new BigDecimal("0.10");
    private static final int SCALE = 2;

    /**
     * 固定値（0.10）のTaxRateを取得
     */
    public static TaxRate fixed() {
        return new TaxRate(FIXED_VALUE);
    }

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static TaxRate ofCreate(BigDecimal value) {
        // TODO: バリデーションエラーをValidationErrorResponseに変換して400エラーを返す
        // - valueがnullの場合
        // - valueの精度がDECIMAL(5,2)を超える場合
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new TaxRate(normalized);
    }
}
