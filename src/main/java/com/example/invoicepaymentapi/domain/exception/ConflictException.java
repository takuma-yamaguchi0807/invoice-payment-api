package com.example.invoicepaymentapi.domain.exception;

/**
 * リソースの競合例外
 * メールアドレスが既に存在する場合など、リソースの競合を表す
 */
public class ConflictException extends RuntimeException {
    private final String messageKey;

    /**
     * メッセージキーを指定して例外を作成
     *
     * @param messageKey メッセージプロパティのキー
     */
    public ConflictException(String messageKey) {
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

