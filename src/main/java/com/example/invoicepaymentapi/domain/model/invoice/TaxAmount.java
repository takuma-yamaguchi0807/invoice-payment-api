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
    public static TaxAmount create(Fee fee, TaxRate taxRate) {
        BigDecimal taxValue = fee.value()
                .multiply(taxRate.value())
                .setScale(SCALE, RoundingMode.HALF_UP);
        return new TaxAmount(taxValue);
    }

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static TaxAmount create(BigDecimal value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new TaxAmount(normalized);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     * 丸め込み後の値で有効範囲をチェックする
     *
     * @param value 消費税
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(BigDecimal value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("taxAmount"));
            return errors;
        }

        // 負の値チェック（丸め込み前の値でチェック）
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            errors.add(new ValidationError("taxAmount", "validation.negative"));
            return errors; // 負の値の場合は、整数部チェックは不要
        }

        // 丸め込み後の値を計算
        BigDecimal rounded = value.setScale(SCALE, RoundingMode.HALF_UP);

        // 整数部の桁数チェック（丸め込み後の値でチェック、15桁 - 2桁 = 13桁）
        BigDecimal integerPart = rounded.setScale(0, RoundingMode.DOWN);
        if (integerPart.precision() > 13) {
            errors.add(new ValidationError("taxAmount", "validation.maxIntegerDigits"));
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約のため、nullが来ることはない
     *
     * @param value 消費税
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static TaxAmount reconstruct(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("TaxAmount cannot be null");
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new TaxAmount(normalized);
    }
}
