package com.example.invoicepaymentapi.presentation.error;

import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 認証エラーレスポンス
 * セキュリティ上の理由により、詳細情報は返さず、codeとmessageのみを返す
 */
public record UnauthorizedErrorResponse(
        @JsonProperty(ApiPropertyNames.CODE)
        String code,
        @JsonProperty(ApiPropertyNames.MESSAGE)
        String message
) {
    /**
     * エラーコード
     */
    public static final String UNAUTHORIZED_ERROR_CODE = "UNAUTHORIZED";
}

