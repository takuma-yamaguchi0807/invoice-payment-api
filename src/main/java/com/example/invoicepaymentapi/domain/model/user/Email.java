package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * メールアドレス値オブジェクト
 */
public record Email(String value) {
    private static final Logger log = LoggerFactory.getLogger(Email.class);
    private static final int MAX_LENGTH = 255;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static Email create(String value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        return new Email(value);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value メールアドレス
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(String value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null || value.isEmpty()) {
            errors.add(ValidationError.required("email"));
        } else {
            if (value.length() > MAX_LENGTH) {
                errors.add(new ValidationError("email", "validation.email.maxLength"));
            }
            if (!EMAIL_PATTERN.matcher(value).matches()) {
                errors.add(new ValidationError("email", "validation.email.format"));
            }
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static Email reconstruct(String value) {
        if (value == null) {
            log.error("Email cannot be null. Invalid data detected in database.");
        }
        return new Email(value);
    }
}
