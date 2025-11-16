package com.example.invoicepaymentapi.application.usecase.users.dto;

/**
 * ユーザー登録リクエストDTO
 * presentation層から受け取り、domain層の値オブジェクトに変換するためのDTO
 */
public record RegisterUserRequestDto(
        String companyName,
        String name,
        String email,
        String password
) {
}

