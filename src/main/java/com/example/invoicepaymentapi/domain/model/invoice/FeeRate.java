package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;

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
    public static FeeRate create(BigDecimal value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new FeeRate(normalized);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value 手数料率
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(BigDecimal value) {
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

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static FeeRate reconstruct(BigDecimal value) {
        if (value == null) {
            return new FeeRate(null);
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new FeeRate(normalized);
    }
}