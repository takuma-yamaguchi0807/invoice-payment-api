package com.example.invoicepaymentapi.domain.exception;

/**
 * 認証失敗例外
 * ログイン時の認証失敗を表す
 */
public class UnauthorizedException extends RuntimeException {
    private final String messageKey;

    /**
     * メッセージキーを指定して例外を作成
     *
     * @param messageKey メッセージプロパティのキー
     */
    public UnauthorizedException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    /**
     * メッセージキーを取得
     *
     * @return メッセージプロパティのキー
     */
    public String getMessageKey() {
        return messageKey;
    }
}

