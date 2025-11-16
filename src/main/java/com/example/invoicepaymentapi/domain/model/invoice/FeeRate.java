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
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("feeRate"));
        } else {
            // 精度チェック（DECIMAL(5,2) = 整数部3桁、小数部2桁）
            BigDecimal scaled = value.setScale(SCALE, RoundingMode.DOWN);
            if (scaled.compareTo(value) != 0) {
                errors.add(new ValidationError("feeRate", "validation.feeRate.scale"));
            }

            // 整数部の桁数チェック（5桁 - 2桁 = 3桁）
            BigDecimal integerPart = value.setScale(0, RoundingMode.DOWN);
            if (integerPart.precision() > 3) {
                errors.add(new ValidationError("feeRate", "validation.feeRate.scale"));
            }
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new FeeRate(normalized);
    }
}
