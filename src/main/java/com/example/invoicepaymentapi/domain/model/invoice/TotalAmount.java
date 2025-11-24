package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 請求金額値オブジェクト
 * DECIMAL(15,2)に対応
 */
public record TotalAmount(BigDecimal value) {
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
    public static TotalAmount create(PaymentAmount paymentAmount, Fee fee, TaxAmount taxAmount) {
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
    public static TotalAmount create(BigDecimal value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new TotalAmount(normalized);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     * 丸め込み後の値で有効範囲をチェックする
     *
     * @param value 請求金額
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(BigDecimal value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("totalAmount"));
            return errors;
        }

        // 負の値チェック（丸め込み前の値でチェック）
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            errors.add(new ValidationError("totalAmount", "validation.negative"));
            return errors; // 負の値の場合は、整数部チェックは不要
        }

        // 丸め込み後の値を計算
        BigDecimal rounded = value.setScale(SCALE, RoundingMode.HALF_UP);

        // 整数部の桁数チェック（丸め込み後の値でチェック、15桁 - 2桁 = 13桁）
        BigDecimal integerPart = rounded.setScale(0, RoundingMode.DOWN);
        if (integerPart.precision() > 13) {
            errors.add(new ValidationError("totalAmount", "validation.maxIntegerDigits"));
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約のため、nullが来ることはない
     *
     * @param value 請求金額
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static TotalAmount reconstruct(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("TotalAmount cannot be null");
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new TotalAmount(normalized);
    }
}
