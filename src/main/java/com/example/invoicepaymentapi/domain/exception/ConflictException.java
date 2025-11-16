package com.example.invoicepaymentapi.domain.exception;

/**
 * リソースの競合例外
 * メールアドレスが既に存在する場合など、リソースの競合を表す
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

