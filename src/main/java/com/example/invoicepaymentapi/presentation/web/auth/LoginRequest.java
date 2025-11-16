package com.example.invoicepaymentapi.presentation.web.auth;

/**
 * ログインリクエスト
 */
public record LoginRequest(
        String email,
        String password
) {
}

