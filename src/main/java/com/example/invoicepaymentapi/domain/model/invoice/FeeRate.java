package com.example.invoicepaymentapi.domain.model.invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 手数料率値オブジェクト
 * 固定値: 0.04 (4%)
 * DECIMAL(5,2)に対応
 */
public record FeeRate(BigDecimal value) {
    private static final Logger log = LoggerFactory.getLogger(FeeRate.class);
    private static final BigDecimal FIXED_VALUE = new BigDecimal("0.04");
    private static final int SCALE = 2;

    /**
     * 固定値（0.04）のFeeRateを取得
     */
    public static FeeRate fixed() {
        return new FeeRate(FIXED_VALUE);
    }

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static FeeRate ofCreate(BigDecimal value) {
        // TODO: バリデーションエラーをValidationErrorResponseに変換して400エラーを返す
        // - valueがnullの場合
        // - valueの精度がDECIMAL(5,2)を超える場合
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new FeeRate(normalized);
    }
}
