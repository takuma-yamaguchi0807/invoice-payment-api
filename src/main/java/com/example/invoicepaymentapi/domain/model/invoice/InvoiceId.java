package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;

import java.util.ArrayList;
import java.util.List;

/**
 * 請求書ID値オブジェクト
 */
public record InvoiceId(Integer value) {
    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static InvoiceId create(Integer value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        return new InvoiceId(value);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value 請求書ID
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(Integer value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("invoiceId"));
        } else {
            // 0以下チェック
            if (value <= 0) {
                errors.add(new ValidationError("invoiceId", "validation.invoiceId.zeroOrNegative"));
            }
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約（PRIMARY KEY）のため、nullが来ることはない
     *
     * @param value 請求書ID
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static InvoiceId reconstruct(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("InvoiceId cannot be null");
        }
        return new InvoiceId(value);
    }
}
