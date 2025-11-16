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
 * 請求金額値オブジェクト
 * DECIMAL(15,2)に対応
 */
public record TotalAmount(BigDecimal value) {
    private static final Logger log = LoggerFactory.getLogger(TotalAmount.class);
    private static final int SCALE = 2;

    /**
     * 支払金額・手数料・消費税から請求金額を計算して作成
     * 計算式: total_amount = payment_amount + fee + tax_amount
     *
     * @param paymentAmount 支払金額
     * @param fee 手数料
     * @param taxAmount 消費税
     * @return 請求金額
     */
    public static TotalAmount ofCreate(PaymentAmount paymentAmount, Fee fee, TaxAmount taxAmount) {
        BigDecimal totalValue = paymentAmount.value()
                .add(fee.value())
                .add(taxAmount.value())
                .setScale(SCALE, RoundingMode.HALF_UP);
        return new TotalAmount(totalValue);
    }

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static TotalAmount ofCreate(BigDecimal value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("totalAmount"));
        } else {
            // 負の値チェック
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                errors.add(new ValidationError("totalAmount", "validation.totalAmount.negative"));
            }

            // 精度チェック（DECIMAL(15,2) = 整数部13桁、小数部2桁）
            BigDecimal scaled = value.setScale(SCALE, RoundingMode.DOWN);
            if (scaled.compareTo(value) != 0) {
                errors.add(new ValidationError("totalAmount", "validation.totalAmount.scale"));
            }

            // 整数部の桁数チェック（15桁 - 2桁 = 13桁）
            BigDecimal integerPart = value.setScale(0, RoundingMode.DOWN);
            if (integerPart.precision() > 13) {
                errors.add(new ValidationError("totalAmount", "validation.totalAmount.scale"));
            }
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new TotalAmount(normalized);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static TotalAmount ofGet(BigDecimal value) {
        if (value == null) {
            log.error("TotalAmount cannot be null. Invalid data detected in database.");
            return new TotalAmount(null);
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new TotalAmount(normalized);
    }
}
