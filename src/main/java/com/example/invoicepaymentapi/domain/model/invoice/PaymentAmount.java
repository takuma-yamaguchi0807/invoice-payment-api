package com.example.invoicepaymentapi.domain.model.invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 支払金額値オブジェクト
 * 正の値のみ許可（0.01以上）
 * DECIMAL(15,2)に対応
 */
public record PaymentAmount(BigDecimal value) {
    private static final Logger log = LoggerFactory.getLogger(PaymentAmount.class);
    private static final int SCALE = 2;

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static PaymentAmount ofCreate(BigDecimal value) {
        // TODO: バリデーションエラーをValidationErrorResponseに変換して400エラーを返す
        // - valueがnullの場合
        // - valueが0.01未満の場合
        // - valueの精度がDECIMAL(15,2)を超える場合
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new PaymentAmount(normalized);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static PaymentAmount ofGet(BigDecimal value) {
        if (value == null) {
            log.error("PaymentAmount cannot be null. Invalid data detected in database.");
            return new PaymentAmount(null);
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new PaymentAmount(normalized);
    }
}
