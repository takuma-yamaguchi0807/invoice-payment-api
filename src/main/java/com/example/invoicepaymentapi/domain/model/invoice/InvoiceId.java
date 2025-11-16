package com.example.invoicepaymentapi.domain.model.invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 請求書ID値オブジェクト
 */
public record InvoiceId(Integer value) {
    private static final Logger log = LoggerFactory.getLogger(InvoiceId.class);
    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static InvoiceId ofCreate(Integer value) {
        // TODO: バリデーションエラーをValidationErrorResponseに変換して400エラーを返す
        // - valueがnullの場合
        // - valueが0以下の場合
        return new InvoiceId(value);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static InvoiceId ofGet(Integer value) {
        if (value == null) {
            log.error("InvoiceId cannot be null. Invalid data detected in database.");
        }
        return new InvoiceId(value);
    }
}
