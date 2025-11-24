package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 手数料値オブジェクト
 * DECIMAL(15,2)に対応
 */
public record Fee(BigDecimal value) {
    private static final int SCALE = 2;

    /**
     * 支払金額と手数料率から手数料を計算して作成
     * 計算式: fee = payment_amount * fee_rate
     *
     * @param paymentAmount 支払金額
     * @param feeRate 手数料率
     * @return 手数料
     */
    public static Fee create(PaymentAmount paymentAmount, FeeRate feeRate) {
        BigDecimal feeValue = paymentAmount.value()
                .multiply(feeRate.value())
                .setScale(SCALE, RoundingMode.HALF_UP);
        return new Fee(feeValue);
    }

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static Fee create(BigDecimal value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new Fee(normalized);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     * 丸め込み後の値で有効範囲をチェックする
     *
     * @param value 手数料
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(BigDecimal value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required(ApiPropertyNames.FEE));
            return errors;
        }

        // 負の値チェック（丸め込み前の値でチェック）
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            errors.add(new ValidationError(ApiPropertyNames.FEE, "validation.negative"));
            return errors; // 負の値の場合は、整数部チェックは不要
        }

        // 丸め込み後の値を計算
        BigDecimal rounded = value.setScale(SCALE, RoundingMode.HALF_UP);

        // 整数部の桁数チェック（丸め込み後の値でチェック、15桁 - 2桁 = 13桁）
        BigDecimal integerPart = rounded.setScale(0, RoundingMode.DOWN);
        if (integerPart.precision() > 13) {
            errors.add(new ValidationError(ApiPropertyNames.FEE, "validation.maxIntegerDigits"));
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約のため、nullが来ることはない
     *
     * @param value 手数料
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static Fee reconstruct(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Fee cannot be null");
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new Fee(normalized);
    }
}
