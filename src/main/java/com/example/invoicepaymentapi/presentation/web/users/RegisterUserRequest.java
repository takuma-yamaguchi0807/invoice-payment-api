package com.example.invoicepaymentapi.presentation.web.users;

import com.example.invoicepaymentapi.application.usecase.users.dto.RegisterUserRequestDto;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ユーザー登録リクエスト
 */
public record RegisterUserRequest(
        @JsonProperty(ApiPropertyNames.COMPANY_NAME)
        String companyName,
        @JsonProperty(ApiPropertyNames.NAME)
        String name,
        @JsonProperty(ApiPropertyNames.EMAIL)
        String email,
        @JsonProperty(ApiPropertyNames.PASSWORD)
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

