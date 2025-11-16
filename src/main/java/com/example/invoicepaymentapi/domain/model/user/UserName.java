package com.example.invoicepaymentapi.domain.model.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 氏名値オブジェクト
 */
public record UserName(String value) {
    private static final Logger log = LoggerFactory.getLogger(UserName.class);
    private static final int MAX_LENGTH = 255;

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static UserName ofCreate(String value) {
        // TODO: バリデーションエラーをValidationErrorResponseに変換して400エラーを返す
        // - valueがnullまたは空文字の場合
        // - valueの長さが255文字を超える場合
        return new UserName(value);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static UserName ofGet(String value) {
        if (value == null) {
            log.error("UserName cannot be null. Invalid data detected in database.");
        }
        return new UserName(value);
    }
}
