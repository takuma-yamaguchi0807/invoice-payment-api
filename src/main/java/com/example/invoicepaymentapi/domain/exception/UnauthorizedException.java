package com.example.invoicepaymentapi.domain.exception;

/**
 * 認証失敗例外
 * ログイン時の認証失敗を表す
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

