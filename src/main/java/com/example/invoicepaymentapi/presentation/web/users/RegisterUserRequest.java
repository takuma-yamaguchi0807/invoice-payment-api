package com.example.invoicepaymentapi.presentation.web.users;

import com.example.invoicepaymentapi.application.usecase.users.dto.RegisterUserRequestDto;

/**
 * ユーザー登録リクエスト
 */
public record RegisterUserRequest(
        String companyName,
        String name,
        String email,
        String password
) {
    /**
     * application層のDTOに変換
     *
     * @return RegisterUserRequestDto
     */
    public RegisterUserRequestDto toDto() {
        return new RegisterUserRequestDto(
                companyName,
                name,
                email,
                password
        );
    }
}

