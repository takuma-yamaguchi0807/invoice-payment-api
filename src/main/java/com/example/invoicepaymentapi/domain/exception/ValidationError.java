package com.example.invoicepaymentapi.domain.exception;

/**
 * バリデーションエラー詳細
 * フィールド名とメッセージキーを保持
 */
public record ValidationError(
        String field,
        String messageKey
) {
    /**
     * 必須エラーのメッセージキー
     */
    public static final String REQUIRED_MESSAGE_KEY = "validation.required";

    /**
     * 必須エラーを作成
     *
     * @param field フィールド名
     * @return ValidationError
     */
    public static ValidationError required(String field) {
        return new ValidationError(field, REQUIRED_MESSAGE_KEY);
    }
}

