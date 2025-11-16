package com.example.invoicepaymentapi.domain.model.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static Email ofCreate(String value) {
        // TODO: バリデーションエラーをValidationErrorResponseに変換して400エラーを返す
        // - valueがnullまたは空文字の場合
        // - valueの長さが255文字を超える場合
        // - valueがメールアドレス形式でない場合
        return new Email(value);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static Email ofGet(String value) {
        if (value == null) {
            log.error("Email cannot be null. Invalid data detected in database.");
        }
        return new Email(value);
    }
}
