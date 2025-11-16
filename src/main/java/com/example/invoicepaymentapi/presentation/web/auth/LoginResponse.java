package com.example.invoicepaymentapi.presentation.web.auth;

import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginResponseDto;

/**
 * ログインレスポンス
 */
public record LoginResponse(
        String accessToken
) {
    /**
     * application層のDTOからResponseを作成
     *
     * @param dto LoginResponseDto
     * @return LoginResponse
     */
    public static LoginResponse from(LoginResponseDto dto) {
        return new LoginResponse(dto.accessToken());
    }
}

