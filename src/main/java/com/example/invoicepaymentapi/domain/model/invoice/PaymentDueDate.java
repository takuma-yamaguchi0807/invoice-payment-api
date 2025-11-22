package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 支払期日値オブジェクト
 * 未来の日付のみ許可
 */
public record PaymentDueDate(LocalDate value) {
    /**
     * Stringから支払期日を作成（日付形式チェックを含む）
     * JSONリクエストやクエリパラメータから受け取った文字列をバリデーションして値オブジェクトを作成
     *
     * @param value 日付文字列（ISO形式: yyyy-MM-dd）
     * @return 支払期日値オブジェクト
     * @throws DomainValidationException バリデーションエラーがある場合
     */
    public static PaymentDueDate create(String value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        return new PaymentDueDate(localDate);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 日付形式チェックとビジネスルールチェック（未来の日付のみ許可）を実施
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value 日付文字列（ISO形式: yyyy-MM-dd）
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(String value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null || value.isEmpty()) {
            errors.add(ValidationError.required("paymentDueDate"));
            return errors;
        }

        LocalDate localDate;
        try {
            localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            errors.add(new ValidationError("paymentDueDate", "validation.date.format"));
            return errors;
        }

        // 未来の日付でない場合のチェック（未来の日付のみ許可）
        LocalDate today = LocalDate.now();
        if (!localDate.isAfter(today)) {
            errors.add(new ValidationError("paymentDueDate", "validation.paymentDueDate.notFuture"));
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約のため、nullが来ることはない
     *
     * @param value 支払期日
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static PaymentDueDate reconstruct(LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("PaymentDueDate cannot be null");
        }
        return new PaymentDueDate(value);
    }
}
