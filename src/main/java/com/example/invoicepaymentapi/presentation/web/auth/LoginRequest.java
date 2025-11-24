package com.example.invoicepaymentapi.presentation.web.auth;

import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginRequestDto;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ログインリクエスト
 */
public record LoginRequest(
        @JsonProperty(ApiPropertyNames.EMAIL)
        String email,
        @JsonProperty(ApiPropertyNames.PASSWORD)
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

