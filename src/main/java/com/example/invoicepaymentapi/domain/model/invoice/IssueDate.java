package com.example.invoicepaymentapi.domain.model.invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * 発行日値オブジェクト
 * 過去または今日のみ許可（未来は不可）
 */
public record IssueDate(LocalDate value) {
    private static final Logger log = LoggerFactory.getLogger(IssueDate.class);
    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static IssueDate ofCreate(LocalDate value) {
        // TODO: バリデーションエラーをValidationErrorResponseに変換して400エラーを返す
        // - valueがnullの場合
        // - valueが未来の日付の場合（過去または今日のみ許可）
        return new IssueDate(value);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static IssueDate ofGet(LocalDate value) {
        if (value == null) {
            log.error("IssueDate cannot be null. Invalid data detected in database.");
        }
        return new IssueDate(value);
    }
}
