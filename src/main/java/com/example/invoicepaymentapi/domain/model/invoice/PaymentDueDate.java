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

        if (StringUtils.isEmpty(value)) {
            errors.add(ValidationError.required(ApiPropertyNames.PAYMENT_DUE_DATE));
            return errors;
        }

        LocalDate localDate;
        try {
            localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            errors.add(new ValidationError(ApiPropertyNames.PAYMENT_DUE_DATE, "validation.date.format"));
            return errors;
        }

        // 未来の日付でない場合のチェック（未来の日付のみ許可）
        LocalDate today = LocalDate.now();
        if (!localDate.isAfter(today)) {
            errors.add(new ValidationError(ApiPropertyNames.PAYMENT_DUE_DATE, "validation.paymentDueDate.notFuture"));
        }

        return errors;
    }

    /**
     * デフォルト値で支払期日を作成
     *
     * @return デフォルト値（明日）の支払期日値オブジェクト
     */
    public static PaymentDueDate defaultValue() {
        return reconstruct(LocalDate.now().plusDays(1));
    }

    /**
     * null/空の場合はデフォルト値、それ以外はバリデーションして作成
     * 一覧取得のクエリパラメータ用（paymentDueFrom）に使用
     *
     * @param value 支払期日の文字列（null/空の場合は明日をデフォルト）
     * @return 支払期日値オブジェクト
     */
    public static PaymentDueDate ofCreateOrDefaultFrom(String value) {
        if (StringUtils.isEmpty(value)) {
            return defaultValue();
        }
        return create(value);
    }

    /**
     * null/空の場合はデフォルト値、それ以外はバリデーションして作成
     * 一覧取得のクエリパラメータ用（paymentDueTo）に使用
     * 開始日から1ヶ月後をデフォルト値とする
     *
     * @param value 支払期日の文字列（null/空の場合は開始日から1ヶ月後をデフォルト）
     * @param from 開始日の値オブジェクト（デフォルト値計算用）
     * @return 支払期日値オブジェクト
     */
    public static PaymentDueDate ofCreateOrDefaultTo(String value, PaymentDueDate from) {
        if (StringUtils.isEmpty(value)) {
            return reconstruct(from.value().plusMonths(1));
        }
        return create(value);
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
