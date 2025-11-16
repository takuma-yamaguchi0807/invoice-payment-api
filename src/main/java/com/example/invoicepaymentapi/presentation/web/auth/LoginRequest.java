package com.example.invoicepaymentapi.presentation.web.auth;

import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginRequestDto;

/**
 * ログインリクエスト
 */
public record LoginRequest(
        String email,
        String password
) {
    /**
     * application層のDTOに変換
     *
     * @return LoginRequestDto
     */
    public LoginRequestDto toDto() {
        return new LoginRequestDto(
                email,
                password
        );
    }
}

