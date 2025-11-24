package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 支払期日範囲値オブジェクト
 * 請求書一覧取得時の期間指定を表現する
 * 相関チェック（from <= to）のみを担当する
 */
public record PaymentDueDateRange(PaymentDueDate from, PaymentDueDate to) {
    /**
     * 支払期日範囲を作成（相関チェックのみ）
     * 開始日と終了日の値オブジェクトを受け取り、相関チェック（from <= to）を実施する
     *
     * @param from 開始日の値オブジェクト
     * @param to 終了日の値オブジェクト
     * @return 支払期日範囲値オブジェクト
     * @throws DomainValidationException 相関チェックエラーがある場合
     */
    public static PaymentDueDateRange create(PaymentDueDate from, PaymentDueDate to) {
        // 日付範囲のバリデーション（from <= to）
        List<ValidationError> errors = validateRange(from, to);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        return new PaymentDueDateRange(from, to);
    }

    /**
     * 相関チェック（from <= to）を実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param from 開始日の値オブジェクト
     * @param to 終了日の値オブジェクト
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validateRange(PaymentDueDate from, PaymentDueDate to) {
        List<ValidationError> errors = new ArrayList<>();
        if (from.value().isAfter(to.value())) {
            errors.add(new ValidationError(
                    ApiPropertyNames.PAYMENT_DUE_TO,
                    "validation.paymentDueTo.range"
            ));
        }
        return errors;
    }

    /**
     * 日付文字列のバリデーション
     * PaymentDueDateのvalidateメソッドと同様のロジックだが、フィールド名を指定できる
     * 一覧取得のクエリパラメータ用（paymentDueFrom/paymentDueTo）に使用
     *
     * @param value 日付文字列（ISO形式: yyyy-MM-dd）
     * @param fieldName フィールド名（エラーメッセージ用）
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validateDate(String value, String fieldName) {
        List<ValidationError> errors = new ArrayList<>();

        if (StringUtils.isEmpty(value)) {
            errors.add(ValidationError.required(fieldName));
            return errors;
        }

        LocalDate localDate;
        try {
            localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            errors.add(new ValidationError(fieldName, "validation.date.format"));
            return errors;
        }

        // 未来の日付でない場合のチェック（未来の日付のみ許可）
        LocalDate today = LocalDate.now();
        if (!localDate.isAfter(today)) {
            errors.add(new ValidationError(fieldName, "validation.paymentDueDate.notFuture"));
        }

        return errors;
    }
}

