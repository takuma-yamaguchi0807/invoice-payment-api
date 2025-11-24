package com.example.invoicepaymentapi.presentation.web.auth;

import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginResponseDto;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ログインレスポンス
 */
public record LoginResponse(
        @JsonProperty(ApiPropertyNames.ACCESS_TOKEN)
        String accessToken
) {
    /**
     * application層のDTOからレスポンスを作成
     *
     * @param dto LoginResponseDto
     * @return LoginResponse
     */
    public static LoginResponse from(LoginResponseDto dto) {
        return new LoginResponse(dto.accessToken());
    }
}

