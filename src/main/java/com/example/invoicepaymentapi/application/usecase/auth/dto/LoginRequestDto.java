package com.example.invoicepaymentapi.application.usecase.auth.dto;

/**
 * ログインリクエストDTO
 * presentation層から受け取り、domain層の値オブジェクトに変換するためのDTO
 */
public record LoginRequestDto(
        String email,
        String password
) {
}

