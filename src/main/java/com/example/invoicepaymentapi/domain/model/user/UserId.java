package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ユーザーID値オブジェクト
 */
public record UserId(Integer value) {
    private static final Logger log = LoggerFactory.getLogger(UserId.class);
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
            errors.add(ValidationError.required("userId"));
        } else {
            if (value <= 0) {
                errors.add(new ValidationError("userId", "validation.userId.zeroOrNegative"));
            }
        }
        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static UserId reconstruct(Integer value) {
        if (value == null) {
            log.error("UserId cannot be null. Invalid data detected in database.");
        }
        return new UserId(value);
    }
}
