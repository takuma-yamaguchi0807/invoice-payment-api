package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 支払期日範囲値オブジェクト
 * 請求書一覧取得時の期間指定を表現する
 * デフォルト値計算、バリデーション、値オブジェクト生成を一括で処理する
 */
public record PaymentDueDateRange(PaymentDueDate from, PaymentDueDate to) {
    /**
     * 文字列から支払期日範囲を作成
     * デフォルト値計算、バリデーション、値オブジェクト生成を一括で処理する
     *
     * @param fromString 開始日の文字列（null/空の場合は明日をデフォルト）
     * @param toString 終了日の文字列（null/空の場合は開始日から1ヶ月後をデフォルト）
     * @return 支払期日範囲値オブジェクト
     * @throws DomainValidationException バリデーションエラーがある場合
     */
    public static PaymentDueDateRange create(String fromString, String toString) {
        // デフォルト値の計算
        PaymentDueDate defaultFrom = PaymentDueDate.defaultFrom();
        String fromDateString = StringUtils.isEmpty(fromString)
                ? defaultFrom.toString()
                : fromString;

        // 開始日のバリデーション
        List<ValidationError> errors = new ArrayList<>();
        errors.addAll(validateDate(fromDateString, "paymentDueFrom"));

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        // 開始日の値オブジェクトを作成
        PaymentDueDate from = PaymentDueDate.create(fromDateString);

        // 終了日のデフォルト値計算（開始日が確定した後に計算）
        String toDateString;
        if (StringUtils.isEmpty(toString)) {
            // paymentDueFromが指定されている場合はその指定日から、未指定の場合はデフォルト開始日から1ヶ月後を計算
            PaymentDueDate defaultTo = PaymentDueDate.defaultTo(from);
            toDateString = defaultTo.toString();
        } else {
            toDateString = toString;
        }

        // 終了日のバリデーション
        errors.addAll(validateDate(toDateString, "paymentDueTo"));

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        // 終了日の値オブジェクトを作成
        PaymentDueDate to = PaymentDueDate.create(toDateString);

        // 日付範囲のバリデーション（from <= to）
        errors = new ArrayList<>();
        if (from.value().isAfter(to.value())) {
            errors.add(new ValidationError(
                    "paymentDueTo",
                    "validation.paymentDueTo.range"
            ));
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        return new PaymentDueDateRange(from, to);
    }

    /**
     * 日付文字列のバリデーション
     * PaymentDueDateのvalidateメソッドと同様のロジックだが、フィールド名を指定できる
     *
     * @param value 日付文字列（ISO形式: yyyy-MM-dd）
     * @param fieldName フィールド名（エラーメッセージ用）
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    private static List<ValidationError> validateDate(String value, String fieldName) {
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

