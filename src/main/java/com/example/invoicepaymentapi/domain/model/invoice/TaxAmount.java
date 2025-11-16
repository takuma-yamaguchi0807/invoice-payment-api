package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 消費税値オブジェクト
 * DECIMAL(15,2)に対応
 */
public record TaxAmount(BigDecimal value) {
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

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static TaxAmount ofCreate(BigDecimal value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("taxAmount"));
        } else {
            // 負の値チェック
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                errors.add(new ValidationError("taxAmount", "validation.taxAmount.negative"));
            }

            // 精度チェック（DECIMAL(15,2) = 整数部13桁、小数部2桁）
            BigDecimal scaled = value.setScale(SCALE, RoundingMode.DOWN);
            if (scaled.compareTo(value) != 0) {
                errors.add(new ValidationError("taxAmount", "validation.taxAmount.scale"));
            }

            // 整数部の桁数チェック（15桁 - 2桁 = 13桁）
            BigDecimal integerPart = value.setScale(0, RoundingMode.DOWN);
            if (integerPart.precision() > 13) {
                errors.add(new ValidationError("taxAmount", "validation.taxAmount.scale"));
            }
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new TaxAmount(normalized);
    }
}
