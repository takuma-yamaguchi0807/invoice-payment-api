package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 発行日値オブジェクト
 * 過去または今日のみ許可（未来は不可）
 */
public record IssueDate(LocalDate value) {
    /**
     * Stringから発行日を作成（日付形式チェックを含む）
     * JSONリクエストやクエリパラメータから受け取った文字列をバリデーションして値オブジェクトを作成
     *
     * @param value 日付文字列（ISO形式: yyyy-MM-dd）
     * @return 発行日値オブジェクト
     * @throws DomainValidationException バリデーションエラーがある場合
     */
    public static IssueDate create(String value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        return new IssueDate(localDate);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 日付形式チェックとビジネスルールチェック（過去または今日のみ許可）を実施
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value 日付文字列（ISO形式: yyyy-MM-dd）
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(String value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null || value.isEmpty()) {
            errors.add(ValidationError.required("issueDate"));
            return errors;
        }

        LocalDate localDate;
        try {
            localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            errors.add(new ValidationError("issueDate", "validation.date.format"));
            return errors;
        }

        // 未来の日付チェック（過去または今日のみ許可）
        LocalDate today = LocalDate.now();
        if (localDate.isAfter(today)) {
            errors.add(new ValidationError("issueDate", "validation.issueDate.future"));
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約のため、nullが来ることはない
     *
     * @param value 発行日
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static IssueDate reconstruct(LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("IssueDate cannot be null");
        }
        return new IssueDate(value);
    }
}
