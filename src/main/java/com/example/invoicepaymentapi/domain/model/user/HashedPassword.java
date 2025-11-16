package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ハッシュ化済みパスワード値オブジェクト
 * Argon2でハッシュ化されたパスワードを保持
 */
public record HashedPassword(String value) {
    private static final Logger log = LoggerFactory.getLogger(HashedPassword.class);
    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static HashedPassword ofCreate(String value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null || value.isEmpty()) {
            errors.add(ValidationError.required("hashedPassword"));
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        return new HashedPassword(value);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static HashedPassword ofGet(String value) {
        if (value == null) {
            log.error("HashedPassword cannot be null. Invalid data detected in database.");
        }
        return new HashedPassword(value);
    }

    @Override
    public String toString() {
        return "HashedPassword{value='***'}";
    }
}
