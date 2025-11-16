package com.example.invoicepaymentapi.presentation.web.users;

/**
 * ユーザー登録リクエスト
 */
public record RegisterUserRequest(
        String companyName,
        String name,
        String email,
        String password
) {
}

