package com.example.invoicepaymentapi.presentation.error;

import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * コンフリクトエラーレスポンス
 * emailが既に存在する場合など、リソースの競合を表す
 */
public record ConflictErrorResponse(
        @JsonProperty(ApiPropertyNames.CODE)
        String code,
        @JsonProperty(ApiPropertyNames.MESSAGE)
        String message
) {
    /**
     * エラーコード
     */
    public static final String CONFLICT_ERROR_CODE = "CONFLICT";
}

