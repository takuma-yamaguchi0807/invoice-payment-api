package com.example.invoicepaymentapi.domain.model.invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 手数料値オブジェクト
 * DECIMAL(15,2)に対応
 *
 * TODO: バリデーションエラーをValidationErrorResponseに変換して400エラーを返す
 * - valueがnullの場合
 * - valueが負の値の場合
 * - valueの精度がDECIMAL(15,2)を超える場合
 */
public record Fee(BigDecimal value) {
    private static final Logger log = LoggerFactory.getLogger(Fee.class);
    private static final int SCALE = 2;

    /**
     * 支払金額と手数料率から手数料を計算して作成
     * 計算式: fee = payment_amount * fee_rate
     *
     * @param paymentAmount 支払金額
     * @param feeRate 手数料率
     * @return 手数料
     */
    public static Fee ofCreate(PaymentAmount paymentAmount, FeeRate feeRate) {
        BigDecimal feeValue = paymentAmount.value()
                .multiply(feeRate.value())
                .setScale(SCALE, RoundingMode.HALF_UP);
        return new Fee(feeValue);
    }
}
