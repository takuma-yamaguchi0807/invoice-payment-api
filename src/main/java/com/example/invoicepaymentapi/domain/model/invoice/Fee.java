package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 手数料値オブジェクト
 * DECIMAL(15,2)に対応
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

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static Fee ofCreate(BigDecimal value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("fee"));
        } else {
            // 負の値チェック
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                errors.add(new ValidationError("fee", "validation.fee.negative"));
            }

            // 精度チェック（DECIMAL(15,2) = 整数部13桁、小数部2桁）
            BigDecimal scaled = value.setScale(SCALE, RoundingMode.DOWN);
            if (scaled.compareTo(value) != 0) {
                errors.add(new ValidationError("fee", "validation.fee.scale"));
            }

            // 整数部の桁数チェック（15桁 - 2桁 = 13桁）
            BigDecimal integerPart = value.setScale(0, RoundingMode.DOWN);
            if (integerPart.precision() > 13) {
                errors.add(new ValidationError("fee", "validation.fee.scale"));
            }
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new Fee(normalized);
    }
}
