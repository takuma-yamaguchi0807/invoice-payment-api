package com.example.invoicepaymentapi.application.usecase.auth.dto;

import com.example.invoicepaymentapi.domain.model.auth.AccessToken;

/**
 * ログインレスポンスDTO
 * domain層のAccessToken値オブジェクトから受け取り、presentation層に渡すためのDTO
 */
public record LoginResponseDto(
        String accessToken
) {
    /**
     * AccessToken値オブジェクトからDTOを作成
     *
     * @param accessToken AccessToken値オブジェクト
     * @return LoginResponseDto
     */
    public static LoginResponseDto from(AccessToken accessToken) {
        return new LoginResponseDto(accessToken.value());
    }
}

