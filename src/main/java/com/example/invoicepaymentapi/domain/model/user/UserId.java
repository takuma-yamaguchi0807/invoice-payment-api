package com.example.invoicepaymentapi.domain.model.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ユーザーID値オブジェクト
 */
public record UserId(Integer value) {
    private static final Logger log = LoggerFactory.getLogger(UserId.class);
    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static UserId ofCreate(Integer value) {
        // TODO: バリデーションエラーをValidationErrorResponseに変換して400エラーを返す
        // - valueがnullの場合
        // - valueが0以下の場合
        return new UserId(value);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static UserId ofGet(Integer value) {
        if (value == null) {
            log.error("UserId cannot be null. Invalid data detected in database.");
        }
        return new UserId(value);
    }
}
