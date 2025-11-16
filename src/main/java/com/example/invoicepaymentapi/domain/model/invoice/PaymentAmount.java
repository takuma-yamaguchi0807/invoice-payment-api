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
 * 支払金額値オブジェクト
 * 正の値のみ許可（0.01以上）
 * DECIMAL(15,2)に対応
 */
public record PaymentAmount(BigDecimal value) {
    private static final Logger log = LoggerFactory.getLogger(PaymentAmount.class);
    private static final int SCALE = 2;

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static PaymentAmount ofCreate(BigDecimal value) {
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
     *
     * @param value 支払金額
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(BigDecimal value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("paymentAmount"));
        } else {
            // 0.01未満のチェック
            if (value.compareTo(new BigDecimal("0.01")) < 0) {
                errors.add(new ValidationError("paymentAmount", "validation.paymentAmount.min"));
            }

            // 精度チェック（DECIMAL(15,2) = 整数部13桁、小数部2桁）
            BigDecimal scaled = value.setScale(SCALE, RoundingMode.DOWN);
            if (scaled.compareTo(value) != 0) {
                errors.add(new ValidationError("paymentAmount", "validation.paymentAmount.scale"));
            }

            // 整数部の桁数チェック（15桁 - 2桁 = 13桁）
            BigDecimal integerPart = value.setScale(0, RoundingMode.DOWN);
            if (integerPart.precision() > 13) {
                errors.add(new ValidationError("paymentAmount", "validation.paymentAmount.scale"));
            }
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static PaymentAmount ofGet(BigDecimal value) {
        if (value == null) {
            log.error("PaymentAmount cannot be null. Invalid data detected in database.");
            return new PaymentAmount(null);
        }
        BigDecimal normalized = value.setScale(SCALE, RoundingMode.HALF_UP);
        return new PaymentAmount(normalized);
    }
}
