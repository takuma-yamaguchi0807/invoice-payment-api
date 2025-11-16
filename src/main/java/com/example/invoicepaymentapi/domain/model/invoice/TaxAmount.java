package com.example.invoicepaymentapi.domain.model.invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 消費税値オブジェクト
 * DECIMAL(15,2)に対応
 *
 * TODO: バリデーションエラーをValidationErrorResponseに変換して400エラーを返す
 * - valueがnullの場合
 * - valueが負の値の場合
 * - valueの精度がDECIMAL(15,2)を超える場合
 */
public record TaxAmount(BigDecimal value) {
    private static final Logger log = LoggerFactory.getLogger(TaxAmount.class);
    private static final int SCALE = 2;

    /**
     * 手数料と消費税率から消費税を計算して作成
     * 計算式: tax_amount = fee * tax_rate
     *
     * @param fee 手数料
     * @param taxRate 消費税率
     * @return 消費税
     */
    public static TaxAmount ofCreate(Fee fee, TaxRate taxRate) {
        BigDecimal taxValue = fee.value()
                .multiply(taxRate.value())
                .setScale(SCALE, RoundingMode.HALF_UP);
        return new TaxAmount(taxValue);
    }
}
