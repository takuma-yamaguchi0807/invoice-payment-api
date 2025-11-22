package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 支払金額値オブジェクト
 * 正の値のみ許可（0.01以上）
 * DECIMAL(15,2)に対応
 */
public record PaymentAmount(BigDecimal value) {
    private static final int SCALE = 2;

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static PaymentAmount create(BigDecimal value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new PaymentAmount(normalized);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     * 丸め込み可能な値（小数部3桁以下）は許容し、丸め込み前の値で0.01未満チェック、丸め込み後の値で有効範囲をチェックする
     *
     * @param value 支払金額
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(BigDecimal value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("paymentAmount"));
        } else {
            // 0.01未満のチェック（丸め込み前の値でチェック）
            if (value.compareTo(new BigDecimal("0.01")) < 0) {
                errors.add(new ValidationError("paymentAmount", "validation.paymentAmount.min"));
            }

            // 丸め込み後の値を計算
            BigDecimal rounded = value.setScale(SCALE, RoundingMode.HALF_UP);

            // 精度チェック（小数部が3桁以下であることを確認）
            // 丸め込み可能な範囲内（3桁以下）なら許容
            int scale = value.scale();
            if (scale > 3) {
                errors.add(new ValidationError("paymentAmount", "validation.paymentAmount.scale"));
            }

            // 整数部の桁数チェック（丸め込み後の値でチェック、15桁 - 2桁 = 13桁）
            BigDecimal integerPart = rounded.setScale(0, RoundingMode.DOWN);
            if (integerPart.precision() > 13) {
                errors.add(new ValidationError("paymentAmount", "validation.paymentAmount.scale"));
            }
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約のため、nullが来ることはない
     *
     * @param value 支払金額
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static PaymentAmount reconstruct(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("PaymentAmount cannot be null");
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new PaymentAmount(normalized);
    }
}
