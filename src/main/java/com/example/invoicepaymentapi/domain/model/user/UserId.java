package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;

import java.util.ArrayList;
import java.util.List;

/**
 * ユーザーID値オブジェクト
 */
public record UserId(Integer value) {
    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static UserId create(Integer value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        return new UserId(value);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value ユーザーID
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(Integer value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required(ApiPropertyNames.USER_ID));
        } else {
            if (value <= 0) {
                errors.add(new ValidationError(ApiPropertyNames.USER_ID, "validation.userId.zeroOrNegative"));
            }
        }
        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約（PRIMARY KEY）のため、nullが来ることはない
     *
     * @param value ユーザーID
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static UserId reconstruct(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new UserId(value);
    }
}
