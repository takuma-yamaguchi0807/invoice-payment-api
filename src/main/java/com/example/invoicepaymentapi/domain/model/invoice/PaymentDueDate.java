package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 支払期日値オブジェクト
 * 未来の日付のみ許可
 */
public record PaymentDueDate(LocalDate value) {
    private static final Logger log = LoggerFactory.getLogger(PaymentDueDate.class);
    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static PaymentDueDate ofCreate(LocalDate value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        return new PaymentDueDate(value);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value 支払期日
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(LocalDate value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("paymentDueDate"));
        } else {
            // 未来の日付でない場合のチェック
            LocalDate today = LocalDate.now();
            if (!value.isAfter(today)) {
                errors.add(new ValidationError("paymentDueDate", "validation.paymentDueDate.notFuture"));
            }
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static PaymentDueDate ofGet(LocalDate value) {
        if (value == null) {
            log.error("PaymentDueDate cannot be null. Invalid data detected in database.");
        }
        return new PaymentDueDate(value);
    }
}
