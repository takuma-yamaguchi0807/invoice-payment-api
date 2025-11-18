package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 消費税率値オブジェクト
 * 固定値: 0.10 (10%)
 * DECIMAL(5,2)に対応
 */
public record TaxRate(BigDecimal value) {
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
    public static TaxRate create(BigDecimal value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new TaxRate(normalized);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value 消費税率
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(BigDecimal value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("taxRate"));
        } else {
            // 精度チェック（DECIMAL(5,2) = 整数部3桁、小数部2桁）
            BigDecimal scaled = value.setScale(SCALE, RoundingMode.DOWN);
            if (scaled.compareTo(value) != 0) {
                errors.add(new ValidationError("taxRate", "validation.taxRate.scale"));
            }

            // 整数部の桁数チェック（5桁 - 2桁 = 3桁）
            BigDecimal integerPart = value.setScale(0, RoundingMode.DOWN);
            if (integerPart.precision() > 3) {
                errors.add(new ValidationError("taxRate", "validation.taxRate.scale"));
            }
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static TaxRate reconstruct(BigDecimal value) {
        if (value == null) {
            return new TaxRate(null);
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new TaxRate(normalized);
    }
}
