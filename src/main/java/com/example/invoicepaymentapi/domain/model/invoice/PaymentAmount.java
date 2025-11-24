package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;

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
     * リクエスト値（丸め込み前）で最小値チェック、丸め込み後の値で整数部の桁数チェックを行う
     *
     * @param value 支払金額
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(BigDecimal value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required(ApiPropertyNames.PAYMENT_AMOUNT));
            return errors;
        }

        // 0.01未満のチェック（丸め込み前の値でチェック）
        // リクエスト値として受け取る値そのもので検証する
        if (value.compareTo(new BigDecimal("0.01")) < 0) {
            errors.add(new ValidationError(ApiPropertyNames.PAYMENT_AMOUNT, "validation.paymentAmount.min"));
            return errors; // 最小値未満の場合は、整数部チェックは不要
        }

        // 丸め込み後の値を計算
        BigDecimal rounded = value.setScale(SCALE, RoundingMode.HALF_UP);

        // 整数部の桁数チェック（丸め込み後の値でチェック、15桁 - 2桁 = 13桁）
        // 実際に保存される値の制約を確認する
        BigDecimal integerPart = rounded.setScale(0, RoundingMode.DOWN);
        if (integerPart.precision() > 13) {
            errors.add(new ValidationError(ApiPropertyNames.PAYMENT_AMOUNT, "validation.maxIntegerDigits"));
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
